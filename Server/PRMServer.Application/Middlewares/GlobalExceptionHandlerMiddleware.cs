using Microsoft.AspNetCore.Mvc;

namespace EOS.Application.API.Middlewares
{
    public class GlobalExceptionHandlerMiddleware : IMiddleware
    {
        public async Task InvokeAsync(HttpContext context, RequestDelegate next)
        {
            try
            {
                await next(context);
            }
            catch (Exception ex)
            {
                var traceId = Guid.NewGuid();
                context.Response.StatusCode = StatusCodes.Status500InternalServerError;

                List<Exception> exceptions = new();
                void traverse(Exception ex)
                {
                    exceptions.Add(ex);
                    if (ex is AggregateException agg)
                    {
                        foreach (var inner in agg.InnerExceptions)
                        {
                            traverse(inner);
                        }
                    }
                    else if (ex.InnerException != null)
                    {
                        traverse(ex.InnerException);
                    }
                }
                traverse(ex);

                await context.Response.WriteAsJsonAsync(new
                {
                    errors = exceptions.Select(x => new
                    {
                        type = x.GetType().Name,
                        message = x.Message
                    })
                });
            }
        }
    }
}
