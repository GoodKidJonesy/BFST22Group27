package bfst22.vector;

import org.w3c.dom.Node;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
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
    int id2 = 0;
    Address address = null;
    OSMNode osmnode = null;
    private HashMap<Long, OSMWay> id2way = new HashMap<Long, OSMWay>();
    private ArrayList<Address> addresses = new ArrayList<>();
    private ArrayList<OSMWay> highways = new ArrayList<OSMWay>();
    private ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
    private ArrayList<OSMNode> highwayNodeList = new ArrayList<>();
    private HashMap<Long, Vertex> vertexMap = new HashMap<Long, Vertex>();
    private Map<WayType, List<Drawable>> lines = new EnumMap<>(WayType.class);
    private ArrayList<Edge> edgeList = new ArrayList<>();
    private NodeMap id2node = new NodeMap();
    private EdgeWeightedDigraph graf;
    String wayName = null;
    int maxSpeed = 0;
    boolean isHighway = false;

    {
        for (var type : WayType.values())
            lines.put(type, new ArrayList<>());
    }
    List<Runnable> observers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Model(String filename)
            throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();
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


        var nodes = new ArrayList<OSMNode>();
        var rel = new ArrayList<OSMWay>();

        long relID = 0;
        var type = WayType.UNKNOWN;
        KDTree OSMNodeTree = new KDTree();

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
                            osmnode = new OSMNode(id, id2, 0.56f * lon, -lat);
                            id2++;
                            id2node.add(osmnode);
                            Vertex V = new Vertex(id,0.56f*lon, -lat);
                            vertexList.add(V);
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
                                case "natural":
                                    if (v.equals("water"))
                                        type = WayType.LAKE;
                                    else if (v.equals("coastline"))
                                        type = WayType.COASTLINE;
                                    break;
                                case "building":
                                    type = WayType.BUILDING;
                                    break;
                                case "highway":
                                        isHighway = true;



                                    if (v.equals("primary") || v.equals("trunk") || v.equals("secondary")
                                            || v.equals("trunk_link") || v.equals("secondary_link")) {
                                        type = WayType.HIGHWWAY;
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
                                case "name":
                                    wayName = v;
                                    break;
                                case "maxspeed":
                                    maxSpeed = Integer.parseInt(v);
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
                            if (isHighway){
                                //System.out.println(osmnode.id);
                                OSMWay highway = new OSMWay(nodes, wayName, maxSpeed);
                                highways.add(highway);
                                isHighway = false;
                            }
                            var way = new PolyLine(nodes);



                            id2way.put(relID, new OSMWay(nodes, wayName, maxSpeed));

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


        System.out.println("Done" + " " + id2);

        createGraph();

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

    public NodeMap getId2node(){
        return id2node;
    }

    public void createGraph(){
        /**
         * constructs Edges from ArrayList highways, and tracks how many vertices there are
         */
        for (OSMWay o : highways){
            for (int j = 0; j < o.nodes.size()-1; j++) {
                double distance = distanceCalc(o.nodes.get(j).getID(), o.nodes.get(j+1).getID());
                Edge e = new Edge(o.nodes.get(j).getID(), o.nodes.get(j+1).getID(), o.nodes.get(j).getID2(), o.nodes.get(j+1).getID2(), o.name, distance/o.getSpeedLimit(), distance);
                e.addFromC(o.nodes.get(j).lat, o.nodes.get(j).lon);
                e.addToC(o.nodes.get(j+1).lat, o.nodes.get(j+1).lon);
                Edge f = new Edge(o.nodes.get(j+1).getID(), o.nodes.get(j).getID(), o.nodes.get(j+1).getID2(), o.nodes.get(j).getID2(), o.name, distance/o.getSpeedLimit(), distance);
                f.addFromC(o.nodes.get(j+1).lat, o.nodes.get(j+1).lon);
                f.addToC(o.nodes.get(j).lat, o.nodes.get(j).lon);
                edgeList.add(e);
                edgeList.add(f);





            }
        }
        graf = new EdgeWeightedDigraph(id2);

        /**
         * Adds edges to the graf.
         */

        for (Edge e : edgeList){
            graf.addEdge(e);
        }
    }

    public EdgeWeightedDigraph getGraf(){
        return graf;
    }

    Double distanceCalc(long from, long to){
        double R = 6371*1000;
        double lat1 = id2node.get(from).lat*Math.PI/180;
        double lat2 = id2node.get(to).lat*Math.PI/180;
        double deltaLat = (lat2-lat1)*Math.PI/180;
        double lon1 = id2node.get(from).lon;
        double lon2 = id2node.get(to).lon;
        double deltaLon = (lon2-lon1) * Math.PI/180;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) *
                Math.cos(lat2) * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;

        return d;
    }
}
