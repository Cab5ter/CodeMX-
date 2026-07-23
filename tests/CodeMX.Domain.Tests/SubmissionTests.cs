using CodeMX.Domain;
using Xunit;

namespace CodeMX.Domain.Tests;

public class SubmissionTests
{
    [Fact]
    public void Status_WhenAllTestsPass_IsAccepted()
    {
        // Arrange
        var challenge = new Challenge("Invertir cadena", Difficulty.Easy);
        var submission = new Submission(challenge, totalTests: 5, passedTests: 5);

        // Act
        SubmissionStatus status = submission.Status;

        // Assert
        Assert.Equal(SubmissionStatus.Accepted, status);
    }

    [Fact]
    public void Status_WhenNoTestsPass_IsRejected()
    {
        // Arrange
        var challenge = new Challenge("Invertir cadena", Difficulty.Easy);
        var submission = new Submission(challenge, totalTests: 5, passedTests: 0);

        // Act
        SubmissionStatus status = submission.Status;

        // Assert
        Assert.Equal(SubmissionStatus.Rejected, status);
    }

    [Fact]
    public void Status_WhenSomeTestsPass_IsPartiallyAccepted()
    {
        // Arrange
        var challenge = new Challenge("Invertir cadena", Difficulty.Medium);
        var submission = new Submission(challenge, totalTests: 4, passedTests: 2);

        // Act
        SubmissionStatus status = submission.Status;

        // Assert
        Assert.Equal(SubmissionStatus.PartiallyAccepted, status);
    }

    [Fact]
    public void Score_IsProportionalToPassedTests()
    {
        // Arrange
        var challenge = new Challenge("Ordenamiento", Difficulty.Medium);
        var submission = new Submission(challenge, totalTests: 4, passedTests: 2);

        // Act
        int score = submission.Score();

        // Assert: 200 * (2/4) = 100
        Assert.Equal(100, score);
    }

    [Fact]
    public void Score_WhenFullyAccepted_EqualsBasePoints()
    {
        // Arrange
        var challenge = new Challenge("Mochila 0/1", Difficulty.Hard);
        var submission = new Submission(challenge, totalTests: 10, passedTests: 10);

        // Act
        int score = submission.Score();

        // Assert
        Assert.Equal(challenge.BasePoints(), score);
    }

    [Fact]
    public void Constructor_WithMorePassedThanTotal_ThrowsOutOfRange()
    {
        // Arrange
        var challenge = new Challenge("Fibonacci", Difficulty.Easy);

        // Act
        var exception = Record.Exception(
            () => new Submission(challenge, totalTests: 3, passedTests: 4));

        // Assert
        Assert.IsType<ArgumentOutOfRangeException>(exception);
    }
}
