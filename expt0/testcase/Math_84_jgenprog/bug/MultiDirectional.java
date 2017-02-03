/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.optimization.direct;

import java.util.Comparator;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import myprinter.FieldPrinter;

/** 
 * This class implements the multi-directional direct search method.
 *
 * @version $Revision$ $Date$
 * @see NelderMead
 * @since 1.2
 */
public class MultiDirectional extends DirectSearchOptimizer {

    public static Map oref_map = new HashMap();

	public static int eid_7au3e = 0;

	public static void addToORefMap(String msig, Object obj) {
		List l = (List) oref_map.get(msig);
		if (l == null) {
			l = new ArrayList();
			oref_map.put(msig, l);
		}
		l.add(obj);
	}

	public static void clearORefMap() {
		oref_map.clear();
		eid_7au3e = 0;
	}

	/** Expansion coefficient. */
    private final double khi;

    /** Contraction coefficient. */
    private final double gamma;

    /** Build a multi-directional optimizer with default coefficients.
     * <p>The default values are 2.0 for khi and 0.5 for gamma.</p>
     */
    public MultiDirectional() {
        this.khi   = 2.0;
        this.gamma = 0.5;
    }

    /** Build a multi-directional optimizer with specified coefficients.
     * @param khi expansion coefficient
     * @param gamma contraction coefficient
     */
    public MultiDirectional(final double khi, final double gamma) {
        this.khi   = khi;
        this.gamma = gamma;
    }

    /** {@inheritDoc} */
    protected void iterateSimplex_7au3e(final Comparator<RealPointValuePair> comparator)
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {

        while (true) {

            incrementIterationsCounter();

            // save the original vertex
            final RealPointValuePair[] original = simplex;
            final RealPointValuePair best = original[0];

            // perform a reflection step
            final RealPointValuePair reflected = evaluateNewSimplex(original, 1.0, comparator);
            if (comparator.compare(reflected, best) < 0) {

                // compute the expanded simplex
                final RealPointValuePair[] reflectedSimplex = simplex;
                final RealPointValuePair expanded = evaluateNewSimplex(original, khi, comparator);
                if (comparator.compare(reflected, expanded) <= 0) {
                    // accept the reflected simplex
                    simplex = reflectedSimplex;
                }

                return;

            }

            // compute the contracted simplex
            final RealPointValuePair contracted = evaluateNewSimplex(original, gamma, comparator);
            if (comparator.compare(contracted, best) < 0) { //Original
                // accept the contracted simplex
                return;
            }

        }

    }

    /** Compute and evaluate a new simplex.
     * @param original original simplex (to be preserved)
     * @param coeff linear coefficient
     * @param comparator comparator to use to sort simplex vertices from best to poorest
     * @return best point in the transformed simplex
     * @exception FunctionEvaluationException if the function cannot be evaluated at
     * some point
     * @exception OptimizationException if the maximal number of evaluations is exceeded
     */
    private RealPointValuePair evaluateNewSimplex(final RealPointValuePair[] original,
                                              final double coeff,
                                              final Comparator<RealPointValuePair> comparator)
        throws FunctionEvaluationException, OptimizationException {

        final double[] xSmallest = original[0].getPointRef();
        final int n = xSmallest.length;

        // create the linearly transformed simplex
        simplex = new RealPointValuePair[n + 1];
        simplex[0] = original[0];
        for (int i = 1; i <= n; ++i) {
            final double[] xOriginal    = original[i].getPointRef();
            final double[] xTransformed = new double[n];
            for (int j = 0; j < n; ++j) {
                xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
            }
            simplex[i] = new RealPointValuePair(xTransformed, Double.NaN, false);
        }

        // evaluate it
        evaluateSimplex(comparator);
        return simplex[0];

    }

	/**
	 * {@inheritDoc}  
	 */
	@Override
	protected void iterateSimplex(
			final Comparator<RealPointValuePair> comparator)
			throws FunctionEvaluationException, OptimizationException,
			IllegalArgumentException {
		Object o_7au3e = null;
		String c_7au3e = "org.apache.commons.math.optimization.direct.MultiDirectional";
		String msig_7au3e = "iterateSimplex(Comparator<RealPointValuePair>)"
				+ eid_7au3e;
		try {
			iterateSimplex_7au3e(comparator);
			addToORefMap(msig_7au3e, null);
			FieldPrinter.print(this, eid_7au3e, c_7au3e, msig_7au3e, 1, 5);
			addToORefMap(msig_7au3e, this);
			addToORefMap(msig_7au3e, null);
		} catch (Throwable t7au3e) {
			FieldPrinter.print(t7au3e, eid_7au3e, c_7au3e, msig_7au3e, 0, 5);
			addToORefMap(msig_7au3e, t7au3e);
			throw t7au3e;
		} finally {
			eid_7au3e++;
		}
	}

}
