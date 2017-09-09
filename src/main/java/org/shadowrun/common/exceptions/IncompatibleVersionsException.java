package org.shadowrun.common.exceptions;

import org.shadowrun.models.Campaign;

public class IncompatibleVersionsException extends Exception {

    private Campaign campaign;

    public IncompatibleVersionsException(Campaign campaign) {
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return campaign;
    }
}
