package com.sri.gui.ext.graph;

import java.awt.*;

/**  Defines a range of values*/
public class Range
{
	public double Upper;
	public double Lower;

	//------------------------------------------------
	// constructors
	//
	public Range()
	{
		Upper = 0;
		Lower = 0;
	}

	public Range(double upperValue)
	{
		Upper = upperValue;
		Lower = 0;
	}

	public Range(double upperValue, double lowerValue)
	{
		Upper = upperValue;
		Lower = lowerValue;
	}

	//------------------------------------------------
	// calculate the nearest 'whole' numbers 
	// of their magnitudes
	//   eq. 5.5 becomes 6, 436 becomes 500
	//
	public void Normalise()
	{
		// Upper value should round up
		double fStep = GetStep(Upper);
		double fNewValue;
		if (Upper != fStep)
		{
			for (fNewValue = fStep; fNewValue <= Upper; fNewValue += fStep)
				;
			Upper = fNewValue;
		}

		// Lower value should round down (if > 0)
		if (Lower > 0)
		{
			fStep = GetStep(Lower);
			if (Lower != fStep)
			{
				for (fNewValue = fStep; fNewValue < Lower; fNewValue += fStep)
					;
				fNewValue -= fStep;	// round down to next lower
				Lower = fNewValue;
			}

			if (Lower == 1)	
				Lower = 0;
		}
	}

	private double GetStep(double value)
	{
		double logIndex = Math.log(value) / Math.log(10);
		logIndex = Math.round(logIndex) - 1;
		if (logIndex < 0)
			logIndex = 0;
		return Math.pow(10, logIndex);
	}

	public double CalculateGradSize()
	{
		// set initial size
		double fGradSize = (long)(Math.log(diff()) / Math.log(10));
		fGradSize = Math.pow(10, fGradSize);

		boolean done = false;
		while (! done)
		{	
			long nGridLines = (long)(diff() / fGradSize);
			if (nGridLines > 12)
				fGradSize *= 2;

			if (nGridLines < 4)
				fGradSize /= 2;

			if (nGridLines >= 4 && nGridLines <= 12)
				done = true;
		}

        // Make sure grad size is a whole number
        if (fGradSize > 1)
            fGradSize = (long)fGradSize;

		return fGradSize;
	}

	//--------------------------------------------
	//  Calculate the diff

	public double diff()
	{
		return Upper - Lower;
	}
}
