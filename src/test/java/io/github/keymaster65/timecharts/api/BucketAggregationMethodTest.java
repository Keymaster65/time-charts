package io.github.keymaster65.timecharts.api;


import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class BucketAggregationMethodTest {

    @Example
    void valueOf() {
        Assertions.assertThat(BucketAggregationMethod.valueOf("AVERAGE")).isEqualTo(BucketAggregationMethod.AVERAGE);
    }
}