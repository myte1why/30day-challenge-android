package info.nivaldoBondanca.challenges30day.content.adapters.item;

/**
 * Created by Nivaldo
 * on 18/04/2014
 */
public class ChallengeInfo {
    public final long challengeID;
    public final long attemptNumber;
    public final long dayNumber;

    public ChallengeInfo(long challengeID, long attemptNumber, long dayNumber) {
        this.challengeID = challengeID;
        this.attemptNumber = attemptNumber;
        this.dayNumber = dayNumber;
    }
}
