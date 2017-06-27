package logic;

import java.util.Comparator;

import data.Job;

public class EDFCompare implements Comparator<Job> {

	@Override
	public int compare(Job arg0, Job arg1) {
		if (arg0.absDeadline == arg1.absDeadline)
			return 0;
		else if (arg0.absDeadline < arg1.absDeadline)
			return -1;
		else
			return 1;
	}

}
