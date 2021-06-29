# osm-imports
### Contains materials and scripts for the manuscript "Analysing the Impact of large Data Imports in OpenStreetMap".

For setting up the OSHDB, please refer to the following manual: https://github.com/GIScience/oshdb/tree/master/documentation/first-steps.

The OSM history file of the Netherlands was downloaded from Geofabrik (https://osm-internal.download.geofabrik.de/europe/netherlands.html) on the 2019-10-15 (size: 2.05 Gigabyte (GB)). Only data from the European mainland was used for the analysis of the Netherlands in this thesis. The OSM history file of India was downloaded from Geofabrik (https://osm-internal.download.geofabrik.de/asia/india.html) on the 2019-10-15 as well (size: 964 Megabyte (MB)).

For transforming the OSM history data files into OSH databases, please refer to the following steps: https://github.com/GIScience/oshdb/tree/master/oshdb-etl.

Afterwards, the scripts in the folder "OSHDB scripts" of the study areas can be executed to reproduce the metrics.
