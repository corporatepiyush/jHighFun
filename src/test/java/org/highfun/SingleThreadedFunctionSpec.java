package org.highfun;

import static org.highfun.util.CollectionUtil.*;
import org.highfun.util.FunctionUtil;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SingleThreadedFunctionSpec {

	@Test
	public void testMapFunctionForList() {

		List<String> list = new LinkedList<String>();
		for (int i = 1; i <= 100; i++) {
			list.add("India");
			list.add("ndia");
			list.add("dia");
			list.add("ia");
			list.add("a");
		}

		List<Character> list1 = FunctionUtil.map(list,
                new Converter<String, Character>() {

                    public Character convert(String input) {
                        try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return input.charAt(0);
                    }
                });

		int i = 0;
		for (Character character : list1) {
			assertTrue(character.charValue() == list.get(i++).charAt(0));
		}
	}

	@Test
     public void testFilterFunctionForList() {

        List<String> list = new LinkedList<String>();
        for (int i = 1; i <= 1000; i++) {
            list.add("Scala");
            list.add("Ruby");
        }

        List<String> list1 = FunctionUtil.filter(list,
                new Condition<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                });

        assertTrue(list1.size() == (list.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

    }

    @Test
    public void testFilterFunctionForSet() {

        Set<String> set = new HashSet<String>();
        for (int i = 1; i <= 1000; i++) {
            set.add("Scala");
            set.add("Ruby");
        }

        Set<String> list1 = FunctionUtil.filter(set,
                new Condition<String>() {

                    public boolean evaluate(String t) {
                        return t.contains("y");
                    }

                });

        assertTrue(list1.size() == (set.size() / 2));

        for (String string : list1) {
            assertTrue(string.equals("Ruby"));
        }

    }

	@Test
	public void testFoldLeftForStringAppend() {

		List<String> list = new LinkedList<String>();
		list.add("Java");
		list.add(" ");
		list.add("Rocks");
		list.add("!");

		StringBuilder stringBuilder = new StringBuilder();

		StringBuilder foldLeft = FunctionUtil.foldLeft(list, stringBuilder,
                new Accumulator<StringBuilder, String>() {

                    public StringBuilder accumulate(StringBuilder accumulator,
                                                    String element) {
                        return accumulator.append(element);
                    }

                });

		assertTrue(foldLeft.toString().equals("Java Rocks!"));

	}

	@Test
	public void testFoldLeftForAdditionOFIntegers() {

		List<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);

		Integer foldLeft = FunctionUtil.foldLeft(list, 0,
                new Accumulator<Integer, Integer>() {

                    public Integer accumulate(Integer accumulator,
                                              Integer element) {
                        return accumulator + element;
                    }

                });

		assertTrue(foldLeft == 10);

	}

	@Test
	public void testFoldRightForStringAppend() {

		List<String> list = new LinkedList<String>();
		list.add("Java");
		list.add(" ");
		list.add("Rocks");
		list.add("!");

		StringBuilder stringBuilder = new StringBuilder();

		StringBuilder foldRight = FunctionUtil.foldRight(list, stringBuilder,
                new Accumulator<StringBuilder, String>() {

                    public StringBuilder accumulate(StringBuilder accumulator,
                                                    String element) {
                        return accumulator.append(element);
                    }

                });

		System.out.println(foldRight);

		assertTrue(foldRight.toString().equals("!Rocks Java"));

	}

	@Test
	public void testFoldRightForAdditionOFIntegers() {

		List<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);

		Integer foldRight = FunctionUtil.foldRight(list, 0,
                new Accumulator<Integer, Integer>() {

                    public Integer accumulate(Integer accumulator,
                                              Integer element) {
                        return accumulator + element;
                    }

                });

		assertTrue(foldRight == 10);

	}

	@Test
	public void testForSortForList() {
		List<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(4);
		list.add(2);
		list.add(3);

		list = FunctionUtil.sort(list, new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        });

		assertTrue(list.toString().equals("[1, 2, 3, 4]"));

	}

    @Test
    public void testForSortForSet() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(4);
        set.add(2);
        set.add(3);

        set = FunctionUtil.sort(set, new Comparator<Integer>() {

            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        });

        assertTrue(set.toString().equals("[1, 2, 3, 4]"));

    }
		
	@Test
	public void testEveryFunction(){
		
		List<String> list = new LinkedList<String>();
		for (int i = 1; i <= 10; i++) {
			list.add("Scala");
			list.add("Java");
		}
		
		boolean bool = FunctionUtil.every(list, new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("v");
            }
        });
		
		assertTrue(!bool);

		bool = FunctionUtil.every(list, new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

		assertTrue(bool);
	}
	
	@Test
	public void testSomeFunction(){
		
		List<String> list = new LinkedList<String>();
		for (int i = 1; i <= 10; i++) {
			list.add("Scala");
			list.add("Java");
		}
		
		boolean bool = FunctionUtil.any(list, new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("R");
            }
        });
		
		assertTrue(!bool);

		bool = FunctionUtil.any(list, new Condition<String>() {

            public boolean evaluate(String string) {
                return string.contains("a");
            }
        });

		assertTrue(bool);
	}

    @Test
    public void testEachFunction(){

        List<String> list = new LinkedList<String>();
            list.add("Scala");
            list.add("Java");

        final List<String> temp = new LinkedList<String>();

        FunctionUtil.each(list, new ItemRecord<String>() {
            public void process(String item) {
                temp.add(item);
            }
        });

        assertEquals(list, temp);
    }


    @Test
    public void testEachFunctionForMap(){

        Map<String, String> map = new HashMap<String, String>();
        map.put("IN", "India");
        map.put("US", "United States");

        final Map<String, String> temp = new HashMap<String, String>();

        FunctionUtil.each(map, new KeyValueRecord<String, String>() {
            public void process(String key, String value) {
                temp.put(key, value);
            }
        });

        assertEquals(map, temp);
    }

    @Test
    public void testCount(){

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        final Set<String> temp = new HashSet<String>();

        int count = FunctionUtil.count(set, new Condition<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

        assertEquals(count, 1);
    }

    @Test
    public void testSplit(){

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        final Set<String> temp = new HashSet<String>();

        Collection<Collection<String>> splits = FunctionUtil.split(set, new Condition<String>() {
            public boolean evaluate(String s) {
                return s.contains("Scala");
            }
        });

        int i=0;
        for (Collection<String> split : splits){
            if(i==0){
                assertEquals(split.toString(),"[Scala]");
            }else{
                assertEquals(split.toString(),"[Java]");
            }
        }
    }

    @Test
    public void testChain(){

        Set<String> set = new HashSet<String>();
        set.add("Scala");
        set.add("Java");

        FunctionChain<String> chain = FunctionUtil.chain(set);

        Collection<String> expected = chain.unchain();

        assertEquals(set, expected);
    }

    @Test
    public void testCurry(){

        CurriedFunction<Integer,Integer> addToFive = FunctionUtil.curry(new Function<Integer,Integer>(){
                       public Integer apply(List<Integer> integers){
                            int sum =0;
                            for(Integer i : integers){
                                sum = sum + i;
                            }
                           return sum;
                       }
        }, List(5));

        assertTrue(addToFive.call(List(10))==15);
        assertTrue(addToFive.call(List(15))==20);

        CurriedFunction<Integer,Integer> addToZero = FunctionUtil.curry(new Function<Integer,Integer>(){
            public Integer apply(List<Integer> integers){
                int sum =0;
                for(Integer i : integers){
                    sum = sum + i;
                }
                return sum;
            }
        }, 0,0);

        assertTrue(addToZero.call(List(10))==10);
        assertTrue(addToZero.call(List(15))==15);
        assertTrue(addToZero.call(List(0))==0);
        assertTrue(addToZero.call(5)==5);
        assertTrue(addToZero.call(5,10)==15);
    }
}
