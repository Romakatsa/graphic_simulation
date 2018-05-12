package com.ngeneration.graphic.engine.commands;

import java.util.function.Function;

import static java.lang.Math.*;

public interface Command {
//    interface IncomingValueConverter extends Function<Double, Double> {
//
//    }
//
//    interface ReversalIncomingValueConverter extends Function<Double, Double> {
//
//    }

    interface Converter /*extends IncomingValueConverter, ReversalIncomingValueConverter*/ {
        Double convert(Double x);
        Double reverse(Double y);
    }

    class LinearConverter implements Converter {
        private static final double K_DEFAULT = 1;
        private final double k;

        public LinearConverter() {
            this(K_DEFAULT);
        }

        public LinearConverter(double k) {
            this.k = k;
        }

        @Override
        public Double convert(Double x) {
            return k * x;
        }

        public Double reverse(Double y) {
            return y / k;
        }
    }

    class ExponentialConverter implements Converter {
        private static final double K_DEFAULT = 0.014;
        private static final double P_DEFAULT = 1.3;
        private final double k;
        private final double p;

        public ExponentialConverter() {
            this(K_DEFAULT, P_DEFAULT);
        }

        public ExponentialConverter(double k, double p) {
            this.k = k;
            this.p = p;
        }

        @Override
        public Double convert(Double x) {// [0;100] -> [0;250]
            return exp(k * pow(x, p)) - 1;
        }

        public Double reverse(Double y) {
            return pow((log(y + 1) / k), 1d / p);
        }
    }
}
