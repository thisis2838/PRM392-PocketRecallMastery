using System.Diagnostics;
using System.Reflection;

namespace EOS.Application.API.Utilities.Helpers
{
    public static class AppBuilderHelpers
    {
        public static void AutoAddServices(this WebApplicationBuilder builder)
        {
            var addScopedFunc = typeof(ServiceCollectionServiceExtensions)
                .GetMethods()
                .First
                (x =>
                    x.Name == nameof(ServiceCollectionServiceExtensions.AddScoped)
                        && x.ContainsGenericParameters
                        && x.GetParameters().Length == 1
                );

            var assem = Assembly.GetCallingAssembly();
            var types = assem.GetTypes();
            var serviceInterfaces = types.Where(x => x.IsInterface && x.Name.EndsWith("Service"));
            foreach (var serviceInterface in serviceInterfaces)
            {
                Debug.WriteLine($"Trying to get implementation class of {serviceInterface.Name}");
                var implementationType = types.SingleOrDefault(x => x.GetInterfaces().Contains(serviceInterface));
                if (implementationType is null)
                {
                    Debug.WriteLine($"No implementation found for {serviceInterface.Name}!");
                    continue;
                }
                addScopedFunc.MakeGenericMethod(serviceInterface, implementationType).Invoke(null, [builder.Services]);
            }
        }
    }
}
