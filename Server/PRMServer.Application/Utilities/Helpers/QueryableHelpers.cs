namespace PRMServer.Application.Utilities.Helpers
{
    public static class QueryableHelpers
    {
        public static IQueryable<T> ApplyPagination<T>(this IQueryable<T> query, int pageIndex, int pageSize)
        {
            if (pageIndex < 0 || pageSize <= 0)
                throw new ArgumentOutOfRangeException("Page index and size must be non-negative and greater than zero.");
            return query.Skip(pageIndex * pageSize).Take(pageSize);
        }
    }
}
