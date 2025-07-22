using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.Extensions.Caching.Memory;
using PRMServer.Application.Services.Contracts;

namespace PRMServer.Application.Services
{
    public class OtpService : IOtpService
    {
        private readonly IMemoryCache _cache;
        private readonly IEmailSender _emailSender;
        private readonly ILogger<OtpService> _logger;

        public OtpService(IMemoryCache cache, IEmailSender emailSender, ILogger<OtpService> logger)
        {
            _cache = cache;
            _emailSender = emailSender;
            _logger = logger;
        }

        public async Task GenerateAndSendOtp(string email, string purpose)
        {
            var otp = new Random().Next(100000, 999999).ToString();
            var cacheKey = $"otp:{email}:{purpose}";

            var options = new MemoryCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromMinutes(5));

            _cache.Set(cacheKey, otp, options);

            _logger.LogInformation($"[OTP Generation] Email: {email}, OTP: {otp}, Purpose: {purpose}");

            await _emailSender.SendEmailAsync(email, "Your OTP Code", $"Your OTP is: <strong>{otp}</strong>");
        }

        public Task<bool> VerifyOtp(string email, string otp, string purpose)
        {
            _logger.LogInformation($"[OTP Verification] Email: {email}, Provided OTP: {otp}, Purpose: {purpose}");
            var cacheKey = $"otp:{email}:{purpose}";
            if (_cache.TryGetValue(cacheKey, out string? storedOtp) && storedOtp == otp)
            {
                _cache.Remove(cacheKey); // Prevent reuse
                return Task.FromResult(true);
            }

            return Task.FromResult(false);
        }
    }
}
