import org.heigit.bigspatialdata.oshdb.api.db.OSHDBDatabase;
import org.heigit.bigspatialdata.oshdb.api.db.OSHDBH2;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMContributionView;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMEntitySnapshotView;
import org.heigit.bigspatialdata.oshdb.api.object.OSMContribution;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.OSHDBBoundingBox;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTagKey;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.celliterator.ContributionType;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.TagTranslator;
import org.heigit.bigspatialdata.oshdb.util.time.OSHDBTimestamps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class OSHDBContributiontypes {
    public static void main(String [] args) throws Exception {
        OSHDBDatabase oshdb = new OSHDBH2("PATH-TO-OSHDB-FILE/netherlands.oshdb");

        Class.forName("org.h2.Driver");

        Connection conn =
                DriverManager.getConnection("jdbc:h2:PATH-TO-OSHDB-FILE/netherlands.oshdb;ACCESS_MODE_DATA=r", "sa", "");

        TagTranslator instance = new TagTranslator(conn);

        int sourceInt = instance.getOSHDBTagOf("source", "AND").getKey();
        int andInt = instance.getOSHDBTagOf("source", "AND").getValue();
        int shapesInt = instance.getOSHDBTagOf("source", "3dShapes").getValue();
        int bagInt = instance.getOSHDBTagOf("source", "BAG").getValue();
        OSHDBTagKey andNosr = instance.getOSHDBTagKeyOf("AND_nosr_r");
        OSHDBTagKey and_Nodes= instance.getOSHDBTagKeyOf("AND_nodes");
        OSHDBTagKey andImportance = instance.getOSHDBTagKeyOf("AND:importance_level");
        OSHDBTagKey shapes = instance.getOSHDBTagKeyOf("3dshapes:ggmodelk");

        /*
        Contribution types:
        - Filter for imports
        */

        SortedMap<OSHDBTimestamp, List<EnumSet<ContributionType>>> r = OSMContributionView.on(oshdb)
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-08-01", "2007-11-02", OSHDBTimestamps.Interval.DAILY) // AND
                //.timestamps("2009-11-01", "2011-03-11", OSHDBTimestamps.Interval.WEEKLY) // 3dShapes
                //.timestamps("2014-02-01", "2014-09-02", OSHDBTimestamps.Interval.DAILY) // BAG
                //.timestamps("2012-02-01", "2012-09-02", OSHDBTimestamps.Interval.DAILY) // no import

                // filter for imports
                .osmEntityFilter(k -> k.hasTagValue(sourceInt, andInt) || k.hasTagKey(andNosr) || k.hasTagKey(and_Nodes) || k.hasTagKey(andImportance))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, shapesInt) || k.hasTagKey(shapes))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, bagInt))

                .map(OSMContribution::getContributionTypes)

                .aggregateByTimestamp()

                .collect();

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Map.Entry<OSHDBTimestamp, List<EnumSet<ContributionType>>> m : r.entrySet()) {
            for (EnumSet<ContributionType> e : m.getValue()) {
                String s = e.toString();
                Integer count = map.get(s);
                if (count == null) {
                    map.put(s, 1);
                } else {
                    map.put(s, count + 1);
                }
            }
            System.out.print("\n" + m.getKey() + ";");
            map.entrySet().stream().forEach(s -> System.out.print(s + "/"));
            map.clear();
        }
    }
}
