public record DatapointSummary(
        double mean,
        double standardDeviation,
        double min,
        double firstQuartile,
        double median,
        double secondQuartile,
        double max) {
}
