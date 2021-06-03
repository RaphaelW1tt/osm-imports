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
        Count of contributor activity:
        - OSMContributionView
        - .map(k -> k.getContributorUserId())
        - countUniq()

        Count of elements:
        - OSMEntitySnapshotView
        - .map(k -> k.getEntity().getUserId())
        - count()
        */

        SortedMap<OSHDBTimestamp, Integer> r = OSMEntitySnapshotView.on(oshdb) // OSMContributionView
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-08-01", "2007-11-01", OSHDBTimestamps.Interval.DAILY) // AND
                //.timestamps("2009-11-01", "2011-03-01", OSHDBTimestamps.Interval.WEEKLY) // 3dShapes
                //.timestamps("2014-02-01", "2014-09-01", OSHDBTimestamps.Interval.DAILY) // BAG
                //.timestamps("2012-02-01", "2012-09-02", OSHDBTimestamps.Interval.DAILY) // no import

                .map(k -> k.getContributorUserId()) // count of contributor activity
                //.map(k -> k.getEntity().getUserId()) // count of elements

                .aggregateByTimestamp()

                .countUniq(); //count of contributor activity
                //.count(); // count of elements

        for (Map.Entry<OSHDBTimestamp, Integer> entry : r.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
