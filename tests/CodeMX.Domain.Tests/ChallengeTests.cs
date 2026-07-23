using CodeMX.Domain;
using Xunit;

namespace CodeMX.Domain.Tests;

public class ChallengeTests
{
    [Fact]
    public void BasePoints_ForEasyChallenge_Returns100()
    {
        // Arrange
        var challenge = new Challenge("Suma de dos numeros", Difficulty.Easy);

        // Act
        int points = challenge.BasePoints();

        // Assert
        Assert.Equal(100, points);
    }

    [Fact]
    public void BasePoints_ForHardChallenge_Returns300()
    {
        // Arrange
        var challenge = new Challenge("Grafo minimo de expansion", Difficulty.Hard);

        // Act
        int points = challenge.BasePoints();

        // Assert
        Assert.Equal(300, points);
    }

    [Fact]
    public void Constructor_TrimsWhitespaceFromTitle()
    {
        // Arrange
        var rawTitle = "   Palindromo   ";

        // Act
        var challenge = new Challenge(rawTitle, Difficulty.Medium);

        // Assert
        Assert.Equal("Palindromo", challenge.Title);
    }

    [Fact]
    public void Constructor_WithBlankTitle_ThrowsArgumentException()
    {
        // Arrange
        var blankTitle = "   ";

        // Act
        var exception = Record.Exception(
            () => new Challenge(blankTitle, Difficulty.Easy));

        // Assert
        Assert.IsType<ArgumentException>(exception);
    }
}
