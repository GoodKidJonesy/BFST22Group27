package bfst22.vector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.management.relation.RelationType;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.XMLReader;

import javafx.beans.Observable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

public class Model {
    float minlat, minlon, maxlat, maxlon;
    Address address = null;
    TrieTree trie;
    OSMNode osmnode = null;
    List<Address> addresses = new ArrayList<>();
    KDTree kdTree;
    KDTree roadTree;
    Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);
    {
        for (WayType type : WayType.values())
            lines.put(type, new ArrayList<>());
    }
    List<Runnable> observers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Model(String filename)
            throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        long time = -System.nanoTime();
        kdTree = new KDTree();
        roadTree = new KDTree();
        if (filename.endsWith(".zip")) {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) {
            try (ObjectInputStream input = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType, List<Drawable>>) input.readObject();
            }
        }
        time += System.nanoTime();
        System.out.println("Load time: " + (long) (time / 1e6) + " ms");
        if (!filename.endsWith(".obj"))
            save(filename);
    }

    public void save(String basename) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }
    }

    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input));
        NodeMap id2node = new NodeMap();
        Map<Long, OSMWay> id2way = new HashMap<>();
        List<OSMNode> nodes = new ArrayList<>();
        List<OSMWay> coastlines = new ArrayList<>();
        List<OSMWay> rel = new ArrayList<>();
        long relID = 0;
        WayType type = WayType.UNKNOWN;
        var relationType = "";
        var multipolygonWays = new ArrayList<OSMWay>();
        var timeTwo = -System.nanoTime();

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    String name = reader.getLocalName();
                    switch (name) {
                        case "bounds":
                            maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;
                        case "node":
                            long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            osmnode = new OSMNode(id, 0.56f * lon, -lat);
                            id2node.add(osmnode);
                            break;
                        case "nd":
                            long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            type = WayType.UNKNOWN;
                            break;
                        case "tag":
                            String k = reader.getAttributeValue(null, "k");
                            String v = reader.getAttributeValue(null, "v");
                            if (k.equals("type"))
                                relationType = v;
                            switch (k) {
                                case "place":
                                    switch (v) {
                                        case "island":
                                        case "islet":
                                        case "peninsula":
                                            type = WayType.LAND;
                                    }
                                case "natural":
                                    switch (v) {
                                        case "water":
                                        case "wetland":
                                            type = WayType.WETLAND;
                                            break;
                                        case "coastline":
                                            type = WayType.COASTLINE;
                                            break;
                                        case "scrub":
                                            type = WayType.WETLAND;
                                            break;
                                        case "stone":
                                            type = WayType.STONE;
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case "building":
                                    type = WayType.BUILDING;
                                    break;
                                case "aerodrome":
                                    type = WayType.CITY;
                                    break;
                                case "leisure":
                                    type = WayType.FOREST;
                                    break;
                                case "landuse":
                                    switch (v) {
                                        case "forest":
                                        case "meadow":
                                            type = WayType.FOREST;
                                            break;
                                        case "military":
                                            type = WayType.MILITARY;
                                            break;
                                        case "residential":
                                        case "port":
                                            type = WayType.CITY;
                                            break;
                                        case "quarry":
                                            type = WayType.STONE;
                                            break;
                                        default:
                                            type = WayType.LANDUSE;
                                            break;
                                    }
                                    break;
                                case "highway":
                                    switch (v) {
                                        case "primary":
                                        case "trunk":
                                        case "secondary":
                                        case "trunk_link":
                                        case "secondary_link":
                                            type = WayType.HIGHWAY;
                                            break;
                                        case "residential":
                                        case "service":
                                        case "cycleway":
                                        case "tertiary":
                                        case "unclassified":
                                        case "road":
                                        case "tertiary_link":
                                        case "path":
                                        case "track":
                                            type = WayType.CITYWAY;
                                            break;
                                        case "motorway":
                                        case "motorway_link":
                                            type = WayType.MOTORWAY;
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case "addr:city":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setCity(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:postcode":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setPostcode(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:housenumber":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setHousenumber(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                case "addr:street":
                                    if (address == null) {
                                        address = new Address(osmnode);
                                    }
                                    address.setStreet(v.intern());
                                    if (address.isFull()) {
                                        addAddress();
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "member":
                            var member = id2way.get(Long.parseLong(reader.getAttributeValue(null, "ref")));
                            if (member != null) {
                                multipolygonWays.add(member);
                            }
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            OSMWay elm = id2way.get(ref);
                            if (elm != null)
                                rel.add(elm);
                            break;
                        case "relation":
                            id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            if (id == 1305702) {
                                System.out.println("Done");
                            }
                            break;

                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "way":
                            PolyLine way = new PolyLine(nodes, type);
                            id2way.put(relID, new OSMWay(nodes));
                            lines.get(type).add(way);
                            nodes.clear();
                            break;
                        case "relation":
                            if (relationType.equals("multipolygon")) {
                                MultiPolygon multiPolygon = new MultiPolygon(multipolygonWays, type);
                                lines.get(type).add(multiPolygon);
                            }
                            relationType = "";
                            multipolygonWays.clear();
                            rel.clear();
                            break;
                    }
                    break;
            }
        }
        timeTwo += System.nanoTime();
        System.out.println("Parsing Done in " + (long) (timeTwo / 1e6) + "ms.");
        timeTwo = -System.nanoTime();
        makeTrie();
        timeTwo += System.nanoTime();
        System.out.println("TrieTree done in: " + (long) (timeTwo / 1e6) + "ms.");
        timeTwo = -System.nanoTime();
        fillTrees();
        timeTwo += System.nanoTime();
        System.out.println("KDTree filled in: " + (long) (timeTwo / 1e6) + " ms");
    }

    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (Runnable observer : observers) {
            observer.run();
        }
    }

    public Iterable<Drawable> iterable(WayType type) {
        return lines.get(type);
    }

    public void addAddress() {
        // System.out.println(address.getStreet());
        addresses.add(address);
        address = null;
    }

    public void makeTrie() {
        TrieTree trie = new TrieTree();
        for (Address a : addresses) {
            trie.insert(a.toString(), a.getCords());
        }
    }

    public void fillTrees() {
        ArrayList<Drawable> main = new ArrayList<>();
        ArrayList<Drawable> roads = new ArrayList<>();

        for (WayType e : WayType.values()) {
            for (Drawable l : iterable(e)) {
                if (l.getType() == WayType.HIGHWAY || l.getType() == WayType.CITYWAY
                        || l.getType() == WayType.MOTORWAY) {
                    roads.add(l);
                } else {
                    main.add(l);
                }
            }
        }
        roadTree.fillTree(roads, 0);
        kdTree.fillTree(main, 0);
    }
}
