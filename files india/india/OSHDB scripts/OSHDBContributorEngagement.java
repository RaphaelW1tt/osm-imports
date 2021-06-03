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
        OSHDBDatabase oshdb = new OSHDBH2("PATH-TO-OSHDB-FILE/india.oshdb");

        Class.forName("org.h2.Driver");

        Connection conn =
                DriverManager.getConnection("jdbc:h2:PATH-TO-OSHDB-FILE/india.oshdb;ACCESS_MODE_DATA=r", "sa", "");

        TagTranslator instance = new TagTranslator(conn);

        int sourceInt = instance.getOSHDBTagOf("source", "AND").getKey();
        int andInt = instance.getOSHDBTagOf("source", "AND").getValue();
        int pgsInt = instance.getOSHDBTagOf("source", "PGS").getValue();
        OSHDBTagKey highwayInt = instance.getOSHDBTagKeyOf("highway");
        OSHDBTagKey andNosr = instance.getOSHDBTagKeyOf("AND_a_nosr_r");
        OSHDBTagKey andNorsP = instance.getOSHDBTagKeyOf("AND_a_nosr_p");
        OSHDBTagKey andImportance = instance.getOSHDBTagKeyOf("AND:importance_level");
        OSHDBTagKey building = instance.getOSHDBTagKeyOf("building");

        /*
        Contributor engagement:
        - Interval filter
        - Filter for imports (for getting user IDs for interval before or after observation period, remove all import filters)
        */

        SortedMap<OSHDBTimestamp, List<Integer>> r = OSMContributionView.on(oshdb) // OSMContributionView
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-12-01", "2008-02-01", OSHDBTimestamps.Interval.MONTHLY) // PGS
                //.timestamps("2007-01-01", "2007-12-01", OSHDBTimestamps.Interval.MONTHLY) // before PGS
                //.timestamps("2009-02-01", "2009-05-01", OSHDBTimestamps.Interval.MONTHLY) // one year after PGS

                //.timestamps("2008-01-01", "2008-03-01", Interval.MONTHLY) // AND
                //.timestamps("2007-01-01", "2008-01-01", Interval.MONTHLY) // before AND
                //.timestamps("2009-03-01", "2009-06-01", Interval.MONTHLY) // one year after AND

                //.timestamps("2015-04-01", "2018-10-01", OSHDBTimestamps.Interval.MONTHLY) // buildings
                //.timestamps("2007-01-01", "2015-04-01", OSHDBTimestamps.Interval.MONTHLY) // before buildings

                //.timestamps("2015-04-01", "2015-12-30", OSHDBTimestamps.Interval.MONTHLY) // buildings phase 1
                //.timestamps("2007-01-01", "2015-04-01", OSHDBTimestamps.Interval.MONTHLY) // before buildings phase 1
                //.timestamps("2016-12-30", "2017-10-01", OSHDBTimestamps.Interval.MONTHLY) // one year after buildings phase 1

                //.timestamps("2015-12-30", "2016-10-01", OSHDBTimestamps.Interval.MONTHLY) // buildings phase 2
                //.timestamps("2007-01-01", "2015-12-30", OSHDBTimestamps.Interval.MONTHLY) // before buildings phase 2
                //.timestamps("2017-10-01", "2017-07-01", OSHDBTimestamps.Interval.MONTHLY) // one year after buildings phase 2

                //.timestamps("2017-02-22", "2018-10-01", OSHDBTimestamps.Interval.MONTHLY) // buildings phase 3
                //.timestamps("2007-01-01", "2017-02-22", OSHDBTimestamps.Interval.MONTHLY) // before buildings phase 3

                //.timestamps("2012-02-01", "2012-09-01", OSHDBTimestamps.Interval.MONTHLY) // no import
                //.timestamps("2007-01-01", "2012-02-01", OSHDBTimestamps.Interval.MONTHLY) // before no import
                //.timestamps("2013-09-01", "2014-04-01", OSHDBTimestamps.Interval.MONTHLY) // one year after no import

                //.timestamps("2019-05-01", "2019-09-01", Interval.MONTHLY) // last four months of investigation period

                // filter for imports
                .osmEntityFilter(k -> k.hasTagValue(sourceInt, pgsInt))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, andInt) || k.hasTagKey(andNosr) || k.hasTagKey(andNorsP) || k.hasTagKey(andImportance))
                //.osmEntityFilter(k -> k.hasTagKey(building))

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
