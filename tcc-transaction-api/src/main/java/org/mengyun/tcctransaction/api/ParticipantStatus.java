package org.mengyun.tcctransaction.api;

public enum ParticipantStatus {
    TRYING(1), TRY_SUCCESS(2), TRY_FAILED(3);

    private int id;

    ParticipantStatus(int id) {
        this.id = id;
    }

    public static ParticipantStatus valueOf(int id) {

        switch (id) {
            case 2:
                return TRY_SUCCESS;
            case 3:
                return TRY_FAILED;
            default:
                return TRYING;
        }
    }

    public int getId() {
        return id;
    }
}
