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

public class Model {
    float minlat, minlon, maxlat, maxlon;
    Address address = null;
    TrieTree trie;
    OSMNode osmnode = null;
    List<Address> addresses = new ArrayList<>();
    KDTree kdTree;
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
                            if(k.equals("type")) relationType = v;
                            switch (k) {
                                case "natural":
                                    if (v.equals("water"))
                                        type = WayType.LAKE;
                                    else if (v.equals("coastline"))
                                        type = WayType.COASTLINE;
                                    break;
                                case "building":
                                    type = WayType.BUILDING;
                                    break;
                                case "landuse":
                                    if (v.equals("forest") || v.equals("meadow"))
                                        type = WayType.FOREST;
                                    else if (v.equals("military"))
                                        type = WayType.MILITARY;
                                    else if(v.equals("residential") || v.equals("port"))
                                        type = WayType.CITY;
                                    else
                                        type = WayType.LANDUSE;
                                case "highway":
                                    if (v.equals("primary") || v.equals("trunk") || v.equals("secondary")
                                            || v.equals("trunk_link") || v.equals("secondary_link")) {
                                        type = WayType.HIGHWAY;
                                    } else if (v.equals("residential") || v.equals("service") || v.equals("cycleway")
                                            || v.equals("tertiary") || v.equals("unclassified")
                                            || v.equals("tertiary_link") || v.equals("road")) {
                                        type = WayType.CITYWAY;
                                    } else if (v.equals("motorway") || v.equals("motorway_link")) {
                                        type = WayType.MOTORWAY;
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
                            if(member != null){
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
                            if(relationType.equals("multipolygon")){
                                MultiPolygon multiPolygon = new MultiPolygon(multipolygonWays, WayType.FOREST);
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
        test();
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
        addresses.add(address);
        address = null;
    }

    public void makeTrie() {
        TrieTree trie = new TrieTree();
        for (Address a : addresses) {
            trie.insert(a.toString(), a.getCords());
        }
    }

    public void test() {
        ArrayList<Drawable> temp = new ArrayList<>();

        for (WayType e : WayType.values()) {
            for (Drawable l : iterable(e)) {
                temp.add(l);
            }
        }
        kdTree.fillTree(temp, 0);
    }
}
