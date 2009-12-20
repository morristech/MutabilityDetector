package se.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertEquals;
import static se.mutabilitydetector.IAnalysisSession.IsImmutable.PROBABLY;

import java.lang.reflect.Array;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import se.mutabilitydetector.AllChecksRunner;
import se.mutabilitydetector.AnalysisSession;
import se.mutabilitydetector.CheckerRunnerFactory;
import se.mutabilitydetector.IAnalysisSession;
import se.mutabilitydetector.IMutabilityCheckerFactory;
import se.mutabilitydetector.MutabilityCheckerFactory;
import se.mutabilitydetector.IAnalysisSession.IsImmutable;
import se.mutabilitydetector.benchmarks.types.EnumType;

/**
 * This test acts as an overall progress checker as well as a general acceptance
 * of the tool so far. There are several classes used as micro benchmarks to
 * tell if the tool is correct. Once the checker can correctly assess these
 * classes the tool is correct for our definition.
 * 
 * The rules for defining the tool as correct can be found at {@link https
 * ://devweb2009.cis.strath.ac.uk/trac/softeval0/wiki/RulesForImmutability}
 * 
 * The pattern seems to be that either all the classes which are mutable pass
 * the checks, and the single immutable one doesn't. Or the other way round. The
 * tool won't be correct until every check passes.
 * 
 * @author graham
 * 
 */
public class MutabilityCheckerTest {

	private AllChecksRunner checker;
	private CheckerRunnerFactory checkerRunnerFactory;
	private IMutabilityCheckerFactory checkerFactory;

	@Before
	public void setUp() {
		checkerFactory = new MutabilityCheckerFactory();
		checkerRunnerFactory = new CheckerRunnerFactory(null);
	}

	private void assertNotImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY_NOT, true);
	}

	private void assertImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.DEFINITELY, false);
	}
	
	private void assertProbablyImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, PROBABLY, true);
		
	}

	private void assertMaybeImmutable(Class<?> toAnalyse) {
		doAssertion(toAnalyse, IsImmutable.MAYBE, true);
	}		
	
	private void doAssertion(Class<?> toAnalyse, IsImmutable expected, boolean printReasons) {
		IAnalysisSession session = new AnalysisSession(null);
		checker = new AllChecksRunner(checkerFactory, checkerRunnerFactory, toAnalyse);
		checker.runCheckers(session);
		String failure = "Exception " + toAnalyse.getName() + " is expected to be immutable.";
		if(printReasons) failure += "\nReasons:" + checker.reasons();
		assertEquals(failure, expected, checker.isImmutable());

	}


	@Test
	public void testImmutableExample() throws Exception {
		assertImmutable(ImmutableExample.class);
	}

	@Test
	public void testMutableByAssigningAbstractTypeToField() throws Exception {
		assertNotImmutable(MutableByAssigningInterfaceToField.class);
	}

	@Test
	public void testMutableByHavingMutableFieldAssigned() throws Exception {
		assertNotImmutable(MutableByHavingMutableFieldAssigned.class);
	}

	@Test
	public void testMutableByHavingSetterMethod() throws Exception {
		assertNotImmutable(MutableByHavingSetterMethod.class);
	}

	@Test
	public void testMutableByNoCopyOfIndirectlyConstructedField() throws Exception {
		assertNotImmutable(MutableByNoCopyOfIndirectlyConstructedField.class);
	}

	@Test
	public void testMutableByNotBeingFinalClass() throws Exception {
		assertMaybeImmutable(MutableByNotBeingFinalClass.class);
	}

	@Test
	public void testEnumTypesAreImmutable() throws Exception {
		assertImmutable(EnumType.class);
	}

	@Test
	public void testWellKnownJavaTypes() throws Exception {
		assertProbablyImmutable(Integer.class);
		assertImmutable(int.class);
		assertImmutable(Array.class);
		// the hash code field is lazily computed, and renders String mutable
		//assertImmutable(String.class); 
		assertMaybeImmutable(Object.class);
		assertNotImmutable(Date.class);

	}


}
