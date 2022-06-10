import org.junit.Test;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CompactComaDelimitedNumbersTest {

    private static ArrayList<Pair<Integer, Integer>> apply(ArrayList<Pair<Integer, Integer>> result, Integer number) {
        if (result.isEmpty()) {
            result.add(new Pair<>(number, number));
            return result;
        }

        final Pair<Integer, Integer> previous = result.get(result.size() - 1);
        if (previous.first() + 1 == number) {
            result.add(new Pair<>(number, previous.second()));
        } else {
            result.add(new Pair<>(number, number));
        }
        return result;
    }

    @Test
    public void testCompactingNumbersWithJavaStream() {
        //given:
        final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 12, 13, 14, 19);

        //when:
        final List<Object> finalResult;
        finalResult = list.stream()
                // Firstly let's pair every number with a number it starts from in
                // given sequence
                .reduce(new ArrayList<Pair<Integer, Integer>>(), CompactComaDelimitedNumbersTest::apply, (a, b) -> a)
                // Now let's group list of pair into a Map where key is a number 'from' and value is a list of values
                // in given sequence starting from 'from' number
                .stream()
                .collect(Collectors.groupingBy(Pair::second, Collectors.mapping(Pair::first, Collectors.toList())))
                // Finally let's sort entry set and convert into expected format
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    return (e.getValue().size() < 3) ?
                            e.getValue() :
                            Collections.singletonList(String.format("%d-%d", e.getValue().get(0), e.getValue().get(e.getValue().size() - 1)));
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        //then:
        assertThat(finalResult, is(equalTo(Arrays.asList("1-5", "12-14", 19))));

    }

    record Pair<T, K>(T first, K second) {

        @Override
            public String toString() {
                return "Pair{" +
                        "first=" + first +
                        ", second=" + second +
                        '}';
            }
        }
}
