package com.trade.run.steps;

public interface Step<I, O> {
    O execute(I input);
}
