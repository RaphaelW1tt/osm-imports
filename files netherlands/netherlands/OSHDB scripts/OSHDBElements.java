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
        Count of elements:
        - Filter for element
        - Filter for imports
        - Filter for contribution type
        */

        SortedMap<OSHDBTimestamp, Integer> r = OSMContributionView.on(oshdb) // OSMContributionView
                .areaOfInterest(new OSHDBBoundingBox(-180, -90, 180, 90))

                .timestamps("2007-01-01", "2019-09-01", Interval.MONTHLY)

                .osmType(OSMType.NODE) // WAY // RELATION

                .filter(k -> k.is(ContributionType.CREATION)) // DELETION

                // filter for imports
                .osmEntityFilter(k -> k.hasTagValue(sourceInt, andInt) || k.hasTagKey(andNosr) || k.hasTagKey(and_Nodes) || k.hasTagKey(andImportance))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, shapesInt) || k.hasTagKey(shapes))
                //.osmEntityFilter(k -> k.hasTagValue(sourceInt, bagInt))

                .map(k -> k.getContributorUserId())

                .aggregateByTimestamp()

                .count();

        for (Map.Entry<OSHDBTimestamp, Integer> entry : r.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
