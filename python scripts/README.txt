Python scripts for creating plots, the calculation of ratios for the contributor engagement and the peak finding of contribution types.
Python version: 2.7.16.

The scripts need input files being provided via command line.
For example:

python contributiontypes.py "..\files india\india\AND\contribution types\all_elements.txt"
python uniquetagkeys.py "..\files india\india\AND\tag analysis\all-elements.txt"
python determineobservationperiod.py "..\files india\india\AND\elements\india-all-contributioncount.txt"

For contributor engagement, three files need to be provided in the following order:
	1. unique users during the import
	2. unique users before the import
	3. unique users after one year or unique users during last four months
python contributorengagement.py "..\files india\india\AND\contributors\unique users.txt" "..\files india\india\AND\contributors\unique users-BEFOREIMPORT.txt" "..\files india\india\AND\contributors\unique users-AFTERONEYEAR.txt"
python contributorengagement.py "..\files india\india\AND\contributors\unique users.txt" "..\files india\india\AND\contributors\unique users-BEFOREIMPORT.txt" "..\files india\india\unique users-LASTFOURMONTHS.txt"


The following libraries were used:

- numpy (numpy==1.16.4)
- matplotlib (matplotlib==2.2.4)
- scipy (scipy==1.2.2)
- pandas (pandas==0.24.2)