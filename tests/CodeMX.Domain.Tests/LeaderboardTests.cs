using CodeMX.Domain;
using Xunit;

namespace CodeMX.Domain.Tests;

public class LeaderboardTests
{
    [Fact]
    public void Register_KeepsOnlyTheBestScorePerUser()
    {
        // Arrange
        var board = new Leaderboard();

        // Act
        board.Register("ana", 120);
        board.Register("ana", 80);

        // Assert
        Assert.Equal(120, board.ScoreOf("ana"));
    }

    [Fact]
    public void ScoreOf_ForUnknownUser_IsZero()
    {
        // Arrange
        var board = new Leaderboard();

        // Act
        int score = board.ScoreOf("desconocido");

        // Assert
        Assert.Equal(0, score);
    }

    [Fact]
    public void Ranking_OrdersUsersByScoreDescending()
    {
        // Arrange
        var board = new Leaderboard();
        board.Register("ana", 300);
        board.Register("luis", 100);
        board.Register("mario", 200);

        // Act
        var ranking = board.Ranking();

        // Assert
        Assert.Equal(new[] { "ana", "mario", "luis" }, ranking);
    }

    [Fact]
    public void Ranking_BreaksTiesAlphabetically()
    {
        // Arrange
        var board = new Leaderboard();
        board.Register("zoe", 150);
        board.Register("beto", 150);

        // Act
        var ranking = board.Ranking();

        // Assert
        Assert.Equal(new[] { "beto", "zoe" }, ranking);
    }

    [Fact]
    public void Register_WithNegativeScore_ThrowsOutOfRange()
    {
        // Arrange
        var board = new Leaderboard();

        // Act
        var exception = Record.Exception(() => board.Register("ana", -5));

        // Assert
        Assert.IsType<ArgumentOutOfRangeException>(exception);
    }
}
