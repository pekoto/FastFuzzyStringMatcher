package com.github.pekoto.fastfuzzystringmatcher;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.pekoto.fastfuzzystringmatcher.StringMatcher;
import com.github.pekoto.fastfuzzystringmatcher.SearchResultList;

public class StringMatcherTest {

	private static StringMatcher<String> stringMatcher = new StringMatcher<String>();
	
	@BeforeClass
	public static void setupStringMatcher() {
		stringMatcher.add("0123456789", "10 digit long string");
		stringMatcher.add("012345678", "9 digit long string");
		stringMatcher.add("01234567", "8 digit long string");
		stringMatcher.add("0123456", "7 digit long string");
		stringMatcher.add("012345", "6 digit long string");
		stringMatcher.add("01234", "5 digit long string");
		stringMatcher.add("0123", "4 digit long string");
		stringMatcher.add("012", "3 digit long string");
		stringMatcher.add("01", "2 digit long string");
		stringMatcher.add("0", "1 digit long string");
		
		stringMatcher.add("Test", "String with uppercase char");
		stringMatcher.add("test", "String with all lowercase chars");
		
		stringMatcher.add("This is a test", "Multiple word string");
		
		stringMatcher.add("Cat", "Short string");
		stringMatcher.add("Bats", "Slightly longer short string");
	}
	
	@Test
	public void testOneHundredPercentMatching() {
		SearchResultList<String> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("01234", results.get(0).getKeyword());
	}
	
	@Test
	public void testSeventyFivePercentMatching() {
		SearchResultList<String> results = stringMatcher.search("0123456789", 75.0f);
		
		assertEquals(3, results.size());
		assertTrue(results.containsKeyword("0123456789"));
		assertTrue(results.containsKeyword("012345678"));
		assertTrue(results.containsKeyword("01234567"));
	}
	
	@Test
	public void testFiftyPercentMatching() {
		SearchResultList<String> results = stringMatcher.search("0123456789", 50.0f);
		
		assertEquals(6, results.size());
		assertTrue(results.containsKeyword("0123456789"));
		assertTrue(results.containsKeyword("012345678"));
		assertTrue(results.containsKeyword("01234567"));
		assertTrue(results.containsKeyword("0123456"));
		assertTrue(results.containsKeyword("012345"));
		assertTrue(results.containsKeyword("01234"));
	}
	
	@Test
	public void testTwentyFivePercentMatching() {
		SearchResultList<String> results = stringMatcher.search("0123456789", 25.0f);
		
		assertEquals(8, results.size());
		assertTrue(results.containsKeyword("0123456789"));
		assertTrue(results.containsKeyword("012345678"));
		assertTrue(results.containsKeyword("01234567"));
		assertTrue(results.containsKeyword("0123456"));
		assertTrue(results.containsKeyword("012345"));
		assertTrue(results.containsKeyword("01234"));
		assertTrue(results.containsKeyword("0123"));
		assertTrue(results.containsKeyword("012"));
	}

	@Test
	public void testEditDistanceMatching() {
		SearchResultList<String> results = stringMatcher.search("01234", 1);
		
		assertEquals(3, results.size());
		assertTrue(results.containsKeyword("01234"));
		assertTrue(results.containsKeyword("0123"));
		assertTrue(results.containsKeyword("012345"));
	}
	
	@Test
	public void testResultsInDescendingOrder() {
		SearchResultList<String> results = stringMatcher.search("Fat", 2);
		
		assertEquals("Cat", results.get(0).getKeyword());
		assertEquals("Bats", results.get(1).getKeyword());
	}
	
	@Test
	public void testResultPercentage() {
		SearchResultList<String> results = stringMatcher.search("Cat", 2);
		
		// Float equality inaccuracy requires checking against some epsilon
		assertTrue(Math.abs(results.get(0).getMatchPercentage() - 100.0f) < 0.1);
		assertTrue(Math.abs(results.get(1).getMatchPercentage() - 50.0f) < 0.1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddEmptyString() {
		// Throws IllegalArgumentException
		stringMatcher.add("", "Empty string");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddNullString() {
		// Throws IllegalArgumentException
		stringMatcher.add(null, "Null string");
	}
	
	@Test
	public void testCaseSensitive() {
		SearchResultList<String> results = stringMatcher.search("cat", 100.0f);
		
		assertTrue(results.containsKeyword("Cat"));
	}
	
	@Test
	public void testMultipleWords() {
		SearchResultList<String> results = stringMatcher.search("This is a vest", 90.0f);
		
		assertTrue(results.containsKeyword("This is a test"));
	}
	
	@Test
	public void testAssociatedData() {
		SearchResultList<String> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("5 digit long string", results.get(0).getAssociatedData());
	}
	
	@Test
	public void testIgnoreSpaces() {
		StringMatcher<String> ignoreSpacesMatcher = new StringMatcher<String>(MatchingOption.REMOVE_SPACING_AND_LINEBREAKS);
		
		ignoreSpacesMatcher.add("This is a test", "A string with spaces");
		
		SearchResultList<String> results = ignoreSpacesMatcher.search("This is  atest", 100.0f);
		
		assertEquals(1, results.size());
		assertTrue(results.containsKeyword("This is a test"));
	}
	
	@Test
	public void testIgnoreTabs() {
		StringMatcher<String> ignoreTabsMatcher = new StringMatcher<String>(MatchingOption.REMOVE_SPACING_AND_LINEBREAKS);

		ignoreTabsMatcher.add("\t\tThis is some tabbed data", "A string with tabs");
		
		SearchResultList<String> results = ignoreTabsMatcher.search("This is some tabbed \tdata", 100.0f);
		
		assertEquals(1, results.size());
		assertTrue(results.containsKeyword("\t\tThis is some tabbed data"));
	}
	
	@Test
	public void testIgnoreLinebreaks() {
		StringMatcher<String> ignoreLinebreaksMatcher = new StringMatcher<String>(MatchingOption.REMOVE_SPACING_AND_LINEBREAKS);
		
		ignoreLinebreaksMatcher.add("This has\nsome line\nbreaks.", "A string with linebreaks");
		
		SearchResultList<String> results = ignoreLinebreaksMatcher.search("This has some line breaks.", 100.0f);
		
		assertEquals(1, results.size());
		assertTrue(results.containsKeyword("This has\nsome line\nbreaks."));
	}
}