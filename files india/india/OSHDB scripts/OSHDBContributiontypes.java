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
        Contribution types:
        - Filter for imports
        */

        SortedMap<OSHDBTimestamp, List<EnumSet<ContributionType>>> r = OSMContributionView.on(oshdb)
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-12-01", "2008-02-01", OSHDBTimestamps.Interval.DAILY) // PGS
                //.timestamps("2008-01-01", "2008-03-01", Interval.DAILY) // AND
                //.timestamps("2015-04-01", "2018-10-01", OSHDBTimestamps.Interval.WEEKLY) // buildings
                //.timestamps("2015-04-01", "2015-12-30", OSHDBTimestamps.Interval.WEEKLY) // buildings phase 1
                //.timestamps("2015-12-30", "2016-10-01", OSHDBTimestamps.Interval.WEEKLY) // buildings phase 2
                //.timestamps("2017-02-22", "2018-10-01", OSHDBTimestamps.Interval.WEEKLY) // buildings phase 3
                //.timestamps("2012-02-01", "2012-09-01", OSHDBTimestamps.Interval.DAILY) // no import

                // filter for imports
                .osmEntityFilter(k -> k.hasTagValue(sourceInt, pgsInt))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, andInt) || k.hasTagKey(andNosr) || k.hasTagKey(andNorsP) || k.hasTagKey(andImportance))
                //.osmEntityFilter(k -> k.hasTagKey(building))

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
