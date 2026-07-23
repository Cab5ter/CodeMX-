namespace CodeMX.Domain;

/// <summary>
/// Estado de un envio segun cuantos casos de prueba paso.
/// </summary>
public enum SubmissionStatus
{
    Rejected,
    PartiallyAccepted,
    Accepted
}

/// <summary>
/// Representa el envio de una solucion a un reto. El evaluador de codigo
/// reporta cuantos casos de prueba pasaron; a partir de eso el envio calcula
/// su estado y el puntaje obtenido (proporcional a los puntos base del reto).
/// </summary>
public class Submission
{
    public Challenge Challenge { get; }

    public int TotalTests { get; }

    public int PassedTests { get; }

    public Submission(Challenge challenge, int totalTests, int passedTests)
    {
        Challenge = challenge ?? throw new ArgumentNullException(nameof(challenge));

        if (totalTests <= 0)
        {
            throw new ArgumentOutOfRangeException(
                nameof(totalTests), "El reto debe tener al menos un caso de prueba.");
        }

        if (passedTests < 0 || passedTests > totalTests)
        {
            throw new ArgumentOutOfRangeException(
                nameof(passedTests),
                "Los casos aprobados deben estar entre 0 y el total de casos.");
        }

        TotalTests = totalTests;
        PassedTests = passedTests;
    }

    public SubmissionStatus Status
    {
        get
        {
            if (PassedTests == 0)
            {
                return SubmissionStatus.Rejected;
            }

            return PassedTests == TotalTests
                ? SubmissionStatus.Accepted
                : SubmissionStatus.PartiallyAccepted;
        }
    }

    /// <summary>
    /// Puntaje obtenido: los puntos base del reto ponderados por la fraccion
    /// de casos de prueba aprobados, redondeado al entero mas cercano.
    /// </summary>
    public int Score()
    {
        double fraction = (double)PassedTests / TotalTests;
        return (int)Math.Round(Challenge.BasePoints() * fraction,
            MidpointRounding.AwayFromZero);
    }
}
