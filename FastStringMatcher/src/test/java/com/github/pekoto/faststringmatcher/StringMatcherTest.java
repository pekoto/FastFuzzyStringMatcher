package com.github.pekoto.faststringmatcher;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class StringMatcherTest {

	private static StringMatcher<String> stringMatcher = new StringMatcher<String>();
	
	@BeforeClass
	public static void setupStringMatcher() {
		stringMatcher.Add("0123456789", "10 digit long string");
		stringMatcher.Add("012345678", "9 digit long string");
		stringMatcher.Add("01234567", "8 digit long string");
		stringMatcher.Add("0123456", "7 digit long string");
		stringMatcher.Add("012345", "6 digit long string");
		stringMatcher.Add("01234", "5 digit long string");
		stringMatcher.Add("0123", "4 digit long string");
		stringMatcher.Add("012", "3 digit long string");
		stringMatcher.Add("01", "2 digit long string");
		stringMatcher.Add("0", "1 digit long string");
		
		stringMatcher.Add("Test", "String with uppercase char");
		stringMatcher.Add("test", "String with all lowercase chars");
		
		stringMatcher.Add("This is a test", "Multiple word string");
		
		stringMatcher.Add("Cat", "Short string");
		stringMatcher.Add("Bats", "Slightly longer short string");
	}
	
	@Test
	public void testOneHundredPercentMatching() {
		StringSearchResults<String> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("01234", results.get(0).getKeyword());
	}
	
	@Test
	public void testSeventyFivePercentMatching() {
		StringSearchResults<String> results = stringMatcher.search("0123456789", 75.0f);
		
		assertEquals(3, results.size());
		assertTrue(results.containsKeyword("0123456789"));
		assertTrue(results.containsKeyword("012345678"));
		assertTrue(results.containsKeyword("01234567"));
	}
	
	@Test
	public void testFiftyPercentMatching() {
		StringSearchResults<String> results = stringMatcher.search("0123456789", 50.0f);
		
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
		StringSearchResults<String> results = stringMatcher.search("0123456789", 25.0f);
		
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
		StringSearchResults<String> results = stringMatcher.search("01234", 1);
		
		assertEquals(3, results.size());
		assertTrue(results.containsKeyword("01234"));
		assertTrue(results.containsKeyword("0123"));
		assertTrue(results.containsKeyword("012345"));
	}
	
	@Test
	public void testResultsInDescendingOrder() {
		StringSearchResults<String> results = stringMatcher.search("Fat", 2);
		
		assertEquals("Cat", results.get(0).getKeyword());
		assertEquals("Bats", results.get(1).getKeyword());
	}
	
	@Test
	public void testResultPercentage() {
		StringSearchResults<String> results = stringMatcher.search("Cat", 2);
		
		// Float equality inaccuracy requires checking against some epsilon
		assertTrue(Math.abs(results.get(0).getMatchPercentage() - 100.0f) < 0.1);
		assertTrue(Math.abs(results.get(1).getMatchPercentage() - 50.0f) < 0.1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddEmptyString() {
		// Throws IllegalArgumentException
		stringMatcher.Add("", "Empty string");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddNullString() {
		// Throws IllegalArgumentException
		stringMatcher.Add(null, "Null string");
	}
	
	@Test
	public void testCaseSensitive() {
		StringSearchResults<String> results = stringMatcher.search("cat", 100.0f);
		
		assertTrue(results.containsKeyword("Cat"));
	}
	
	@Test
	public void testMultipleWords() {
		StringSearchResults<String> results = stringMatcher.search("This is a vest", 90.0f);
		
		assertTrue(results.containsKeyword("This is a test"));
	}
	
	@Test
	public void testAssociatedData() {
		List<StringSearchResult<String>> results = stringMatcher.search("01234", 100.0f);
		
		assertEquals(1, results.size());
		assertEquals("5 digit long string", results.get(0).getAssociatedData());
	}
}