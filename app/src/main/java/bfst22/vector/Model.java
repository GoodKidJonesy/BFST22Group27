package bfst22.vector;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static java.util.stream.Collectors.toList;

public class Model {
    float minlat, minlon, maxlat, maxlon;
    Address address = null;
    OSMNode osmnode = null;
    ArrayList<Address> addresses = new ArrayList<>();
    KDTree OSMNodeTree;
    Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);
    {
        for (var type : WayType.values())
            lines.put(type, new ArrayList<>());
    }
    List<Runnable> observers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Model(String filename)
            throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();
        OSMNodeTree = new KDTree();
        if (filename.endsWith(".zip")) {
            var zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) {
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType, List<Drawable>>) input.readObject();
            }
        } else {
            lines.put(WayType.UNKNOWN, Files.lines(Paths.get(filename))
                    .map(Line::new)
                    .collect(toList()));
        }
        time += System.nanoTime();
        System.out.println("Load time: " + (long) (time / 1e6) + " ms");
        if (!filename.endsWith(".obj"))
            save(filename);
    }

    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }
    }

    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input));
        var id2node = new NodeMap();
        ArrayList<OSMNode> id2nodeList = new ArrayList<>();
        var id2way = new HashMap<Long, OSMWay>();
        var nodes = new ArrayList<OSMNode>();
        var rel = new ArrayList<OSMWay>();
        long relID = 0;
        var type = WayType.UNKNOWN;

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    var name = reader.getLocalName();
                    switch (name) {
                        case "bounds":
                            maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;
                        case "node":
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            osmnode = new OSMNode(id, 0.56f * lon, -lat);
                            id2node.add(osmnode);
                            id2nodeList.add(osmnode);
                            break;
                        case "nd":
                            var ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            type = WayType.UNKNOWN;
                            break;
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            switch (k) {
                                case "place":
                                    if (v.equals("island"))
                                        type = WayType.ISLAND;
                                    break;
                                case "waterway":
                                    type = WayType.LAKE;
                                    break;
                                case "water":
                                    type = WayType.LAKE;
                                    break;
                                case "natural":
                                    if (v.equals("water"))
                                        type = WayType.LAKE;
                                    else if (v.equals("coastline"))
                                        type = WayType.COASTLINE;
                                    else if (v.equals("scrub") || v.equals("tree_row"))
                                        type = WayType.FOREST;
                                    else if (v.equals("wetland"))
                                        type = WayType.LANDUSE;
                                    else if (v.equals("bare_rock"))
                                        type = WayType.STONE;
                                    break;
                                case "building":
                                    type = WayType.BUILDING;
                                    break;
                                case "leisure":
                                    if (v.equals("pitch"))
                                        type = WayType.FOREST;
                                    break;
                                case "highway":
                                    if (v.equals("primary") || v.equals("trunk") || v.equals("secondary")
                                            || v.equals("trunk_link") || v.equals("secondary_link")) {
                                        type = WayType.HIGHWWAY;
                                    } else if (v.equals("residential") || v.equals("service") || v.equals("cycleway")
                                            || v.equals("tertiary") || v.equals("unclassified")
                                            || v.equals("tertiary_link") || v.equals("road")) {
                                        type = WayType.CITYWAY;
                                    } else if (v.equals("motorway") || v.equals("motorway_link")) {
                                        type = WayType.MOTORWAY;
                                    } else if (v.equals("track") || v.equals("path") || v.equals("footway"))
                                        type = WayType.DIRTTRACK;
                                    break;
                                case "barrier":
                                    if (v.equals("hedge"))
                                        type = WayType.FOREST;
                                    break;
                                case "landuse":
                                    if (v.equals("forest") || v.equals("meadow") || v.equals("allotments"))
                                        type = WayType.FOREST;
                                    else if (v.equals("residential") || v.equals("industrial"))
                                        type = WayType.CITY;
                                    else if (v.equals("port"))
                                        type = WayType.LAKE;
                                    else if (v.equals("quarry"))
                                        type = WayType.STONE;
                                    else
                                        type = WayType.LANDUSE;
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
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref);
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
                            var way = new PolyLine(nodes);
                            id2way.put(relID, new OSMWay(nodes));
                            lines.get(type).add(way);
                            nodes.clear();
                            break;
                        case "relation":
                            if (type == WayType.LAKE && !rel.isEmpty()) {
                                lines.get(type).add(new MultiPolygon(rel));
                            }
                            rel.clear();
                            break;
                    }
                    break;
            }
        }
        System.out.println("Done");
        // System.out.println(id2nodeList.size());
        OSMNodeTree.fillTree(id2nodeList, 0);
        // OSMNodeTree.printTree(OSMNodeTree.getRoot());
        // System.out.println("root: " + OSMNodeTree.getRoot());
    }

    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (var observer : observers) {
            observer.run();
        }
    }

    public Iterable<Drawable> iterable(WayType type) {
        return lines.get(type);
    }

    public void addAddress() {
        addresses.add(address);
        address.setCity(null);
        address.setPostcode(null);
        address.setHousenumber(null);
        address.setStreet(null);
    }
}
