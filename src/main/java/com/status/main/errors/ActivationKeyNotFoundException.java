package com.status.main.errors;

public class ActivationKeyNotFoundException extends BadRequestAlertException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3200648670348044978L;

	public ActivationKeyNotFoundException() {
        super(ErrorConstants.ACTIVATION_KEY__NOT_FOUND, "activation key not found!", "userManagement", "activation key not found");
    }

}
