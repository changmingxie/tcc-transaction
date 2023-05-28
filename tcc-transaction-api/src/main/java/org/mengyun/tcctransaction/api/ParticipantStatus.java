package org.mengyun.tcctransaction.api;

public enum ParticipantStatus {

    TRYING(1),
    CONFIRMING(2),
    CANCELLING(3),
    TRY_SUCCESS(11),
    TRY_FAILED(12),
    CONFIRM_SUCCESS(21),
    CANCEL_SUCCESS(31);

    private int id;

    ParticipantStatus(int id) {
        this.id = id;
    }

    public static ParticipantStatus valueOf(int id) {
        switch(id) {
            case 1:
                return TRYING;
            case 11:
                return TRY_SUCCESS;
            case 12:
                return TRY_FAILED;
            case 2:
                return CONFIRMING;
            case 21:
                return CONFIRM_SUCCESS;
            case 31:
                return CANCEL_SUCCESS;
            default:
                throw new IllegalArgumentException("the id " + id + " of ParticipantStatus is illegal.");
        }
    }

    public int getId() {
        return id;
    }
}
