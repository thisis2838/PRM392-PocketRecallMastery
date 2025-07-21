using System.Diagnostics;
using System.Security.Claims;

namespace PRMServer.Application.Utilities.Helpers
{
    public static class HttpContextHelpers
    {
        public static int? GetUserId(this HttpContext context)
        {
            if (!(context.User.Identity?.IsAuthenticated ?? false))
                return null;

            var claim = context.User.FindFirstValue(ClaimTypes.NameIdentifier);
            Trace.Assert(claim != null);
            return int.Parse(claim);
        }
    }
}
