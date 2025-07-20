namespace PRMServer.Application.Services.Contracts
{
    public interface IOtpService
    {
        Task GenerateAndSendOtp(string email, string purpose);
        Task<bool> VerifyOtp(string email, string otp, string purpose);
    }
}
