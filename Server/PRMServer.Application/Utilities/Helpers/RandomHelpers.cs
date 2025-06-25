using SwapPair = (int A, int B);

namespace EOS.Application.API.Utilities.Helpers
{
    public static class RandomHelpers
    {
        public static IEnumerable<SwapPair> GenerateShuffleSwapPairs(int seed, int count)
        {
            if (count < 0)
                throw new ArgumentOutOfRangeException(nameof(count));
            if (count == 0)
                return Enumerable.Empty<SwapPair>();

            var rand = new Random(seed);
            return Enumerable.Range(0, count - 2).Select(x => (a: x, b: rand.Next(x, count - 1)));
        }
        public static IList<T> Shuffled<T>(this IList<T> input, int seed)
        {
            input = input.ToList();
            foreach (var (a, b) in GenerateShuffleSwapPairs(seed, input.Count))
            {
                (input[a], input[b]) = (input[b], input[a]);
            }
            return input;
        }
        public static IList<T> Unhuffled<T>(this IList<T> input, int seed)
        {
            input = input.ToList();
            foreach (var (a, b) in GenerateShuffleSwapPairs(seed, input.Count).Reverse())
            {
                (input[a], input[b]) = (input[b], input[a]);
            }
            return input;
        }

        public static int ShuffledIndex(int seed, int index, int count)
        {
            if (index < 0 || index >= count)
                throw new ArgumentOutOfRangeException(nameof(seed));

            var rand = new Random(seed);

            // fisher-yates
            return GenerateShuffleSwapPairs(seed, count)
                .Aggregate(index, (ret, swap) =>
                {
                    if (swap.A == ret) return swap.B;
                    else if (swap.B == ret) return swap.A;
                    return ret;
                });
        }

        public static int UnshuffledIndex(int seed, int index, int count)
        {
            if (index < 0 || index >= count)
                throw new ArgumentOutOfRangeException(nameof(seed));

            var rand = new Random(seed);

            // reversed fisher-yates
            return GenerateShuffleSwapPairs(seed, count)
                .Reverse()
                .Aggregate(index, (ret, swap) =>
                {
                    if (swap.A == ret) return swap.B;
                    else if (swap.B == ret) return swap.A;
                    return ret;
                });
        }
    }
}
