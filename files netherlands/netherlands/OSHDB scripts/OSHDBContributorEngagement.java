import org.heigit.bigspatialdata.oshdb.api.db.OSHDBDatabase;
import org.heigit.bigspatialdata.oshdb.api.db.OSHDBH2;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMContributionView;
import org.heigit.bigspatialdata.oshdb.api.mapreducer.OSMEntitySnapshotView;
import org.heigit.bigspatialdata.oshdb.api.object.OSMContribution;
import org.heigit.bigspatialdata.oshdb.osm.OSMType;
import org.heigit.bigspatialdata.oshdb.util.OSHDBBoundingBox;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTagKey;
import org.heigit.bigspatialdata.oshdb.util.OSHDBTimestamp;
import org.heigit.bigspatialdata.oshdb.util.tagtranslator.TagTranslator;
import org.heigit.bigspatialdata.oshdb.util.time.OSHDBTimestamps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class OSHDBContributorActivity {
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
        Contributor engagement:
        - Interval filter
        - Filter for imports (for getting user IDs for interval before or after observation period, remove all import filters)
        */

        SortedMap<OSHDBTimestamp, List<Integer>> r = OSMContributionView.on(oshdb) // OSMContributionView
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-08-01", "2007-11-01", Interval.MONTHLY) // AND
                //.timestamps("2007-01-01", "2007-08-01", Interval.MONTHLY) // before AND
                //.timestamps("2008-11-01", "2009-02-01", Interval.MONTHLY) // one year after AND

                //.timestamps("2009-11-01", "2011-03-01", Interval.MONTHLY) // 3d
                //.timestamps("2007-01-01", "2009-11-01", Interval.MONTHLY) // before 3d
                //.timestamps("2012-03-01", "2013-07-01", Interval.MONTHLY) // one year after 3d

                //.timestamps("2014-02-01", "2014-09-01", Interval.MONTHLY) // BAG
                //.timestamps("2007-01-01", "2014-02-01", Interval.MONTHLY) // before BAG
                //.timestamps("2015-09-01", "2016-04-01", Interval.MONTHLY) // one year after BAG

                //.timestamps("2012-02-01", "2012-09-01", OSHDBTimestamps.Interval.MONTHLY) // no import
                //.timestamps("2007-01-01", "2012-02-01", OSHDBTimestamps.Interval.MONTHLY) // before no import
                //.timestamps("2013-09-01", "2014-04-01", OSHDBTimestamps.Interval.MONTHLY) // one year after no import

                //.timestamps("2019-05-01", "2019-09-01", Interval.MONTHLY) // last four months of investigation period

                // filter for imports
                .osmEntityFilter(k -> k.hasTagValue(sourceInt, andInt) || k.hasTagKey(andNosr) || k.hasTagKey(and_Nodes) || k.hasTagKey(andImportance))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, shapesInt) || k.hasTagKey(shapes))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, bagInt))

                .map(k -> k.getContributorUserId())

                .aggregateByTimestamp()

                .collect();


        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Map.Entry<OSHDBTimestamp, List<Integer>> m : r.entrySet()) {
            for (Integer e : m.getValue()) {
                String s = e.toString();
                Integer count = map.get(s);
                if (count == null) {
                    map.put(s, 1);
                } else {
                    map.put(s, count + 1);
                }
            }
        }
        map.entrySet().stream().forEach(s -> System.out.print(s + " "));
    }
}
