
using System.Reflection;
using System.Text;
using EOS.Application.API.Middlewares;
using EOS.Application.API.Utilities.Helpers;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using PRMServer.Data.Context;
using PRMServer.Data.Models;

namespace PRMServer.Application
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // database
            builder.Services.AddDbContext<PRMContext>(options =>
            {
                options
                    .UseSqlServer(builder.Configuration["ConnectionString"], options2 =>
                    {
                        options2.MigrationsAssembly("PRMServer.Application");
                    })
                    .UseSeeding((context, _) =>
                    {
                        // Check for Admin account and create if not exists
                        var config = builder.Configuration;
                        var adminUsername = config["AdminAccount:Username"];
                        var adminEmail = config["AdminAccount:Email"];
                        var adminPassword = config["AdminAccount:Password"];

                        if (adminUsername != null &&
                            adminEmail != null &&
                            adminPassword != null)
                        {
                            // Replace 'User' with your actual User entity class name
                            // and update property names as needed
                            if (!context.Set<User>().Any(u => u.UserName == adminUsername))
                            {
                                var hasher = new PasswordHasher<User>();
                                var adminUser = new User
                                {
                                    UserName = adminUsername,
                                    NormalizedUserName = adminUsername.ToUpperInvariant(),
                                    Email = adminEmail,
                                    NormalizedEmail = adminEmail.ToUpperInvariant(),
                                    SecurityStamp = Guid.NewGuid().ToString(),
                                };
                                adminUser.PasswordHash = hasher.HashPassword(adminUser, adminPassword);

                                context.Set<User>().Add(adminUser);
                                context.SaveChanges();
                            }
                        }
                    });

            });

            // services
            builder.AutoAddServices();
            builder.Services.AddTransient<GlobalExceptionHandlerMiddleware>();
            builder.Services.AddIdentity<User, IdentityRole<int>>()
                .AddEntityFrameworkStores<PRMContext>()
                .AddDefaultTokenProviders();
            builder.Services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            })
            .AddJwtBearer(options =>
            {
                options.RequireHttpsMetadata = false;
                options.MapInboundClaims = false;
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidateAudience = true,
                    ValidateLifetime = true,
                    ValidateIssuerSigningKey = true,
                    ValidIssuer = builder.Configuration["Jwt:Issuer"],
                    ValidAudience = builder.Configuration["Jwt:Audience"],
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"] ?? throw new Exception("Jwt key not found.")))
                };
                options.Events = new JwtBearerEvents
                {
                    OnMessageReceived = context =>
                    {
                        var authHeader = context.Request.Headers["Authorization"].FirstOrDefault();
                        if (!string.IsNullOrEmpty(authHeader) && authHeader.StartsWith("Bearer "))
                        {
                            context.Token = authHeader.Substring("Bearer ".Length);
                        }
                        return Task.CompletedTask;
                    }
                };
            });

            builder.Services.AddAuthorization();

            // sessions
            builder.Services.AddDistributedMemoryCache();
            builder.Services.AddSession(options =>
            {
                options.IdleTimeout = TimeSpan.FromMinutes(30);
                options.Cookie.HttpOnly = true;
                options.Cookie.IsEssential = true;
            });

            // mappings
            builder.Services.AddAutoMapper(Assembly.GetExecutingAssembly());

            // controllers
            builder.Services.AddControllers();
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // cors
            const string CORS_POLICY_NAME = "default";
            builder.Services.AddCors(options =>
            {
                options.AddPolicy(name: CORS_POLICY_NAME, policy =>
                {
                    policy.AllowAnyHeader();
                    policy.AllowAnyMethod();
                    policy.AllowAnyOrigin();
                });
            });

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseCors(CORS_POLICY_NAME);
            app.UseAuthentication();
            app.UseAuthorization();
            
            app.MapControllers();

            app.Run();
        }
    }
}
