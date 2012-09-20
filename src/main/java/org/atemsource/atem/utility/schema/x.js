x = {
	"ext_type" : "schema",
	"type" : "de.s2.sim.simyo.api.service.rest.order.OrderRequest",
	"attributes" : {
		"communicationPreferences" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "de.s2.sim.simyo.api.domain.preferences.CommunicationPreferences",
			"attributes" : {
				"newsletterType" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"values" : [ "HTML", "TEXT", "NONE" ],
					"array" : false
				},
				"extraDirectMailing" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "boolean",
					"array" : false
				},
				"trafficDataUsage" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "boolean",
					"array" : false
				},
				"mobileMarketing" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "boolean",
					"array" : false
				},
				"phoneMarketing" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "boolean",
					"array" : false
				}
			},
			"array" : false
		},
		"campaign" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "de.s2.sim.simyo.api.domain.order.Campaign",
			"attributes" : {
				"att" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"max-length" : null,
					"array" : false
				},
				"campaignCode" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"max-length" : null,
					"array" : false
				}
			},
			"array" : false
		},
		"mnpInformation" : {
			"ext_type" : "single-attribute",
			"required" : false,
			"type" : "de.s2.sim.simyo.api.domain.mnp.MnpInformation",
			"attributes" : {
				"oldProviderName" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"max-length" : null,
					"array" : false
				},
				"mnpPhone" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "DomainValue",
					"array" : false
				},
				"corporateInformation" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "de.s2.sim.simyo.api.domain.mnp.CorporateInformation",
					"attributes" : {
						"name" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"city" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"street" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"zip" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"streetNo" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"customerNo" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						}
					},
					"array" : false
				},
				"terminationDate" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "org.joda.time.DateTime",
					"format" : "dd-MM-yyyy",
					"array" : false
				}
			},
			"array" : false
		},
		"formfactor" : {
			"ext_type" : "single-attribute",
			"required" : false,
			"type" : "text",
			"values" : [ "MICRO_SIM" ],
			"array" : false
		},
		"desiredPhoneNumber" : {
			"ext_type" : "single-attribute",
			"required" : false,
			"type" : "DomainValue",
			"array" : false
		},
		"contractSettings" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "de.s2.sim.simyo.api.domain.order.ContractSettings",
			"attributes" : {
				"tariff" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"values" : [ "UNIFORM", "BASIC", "ALLNET" ],
					"array" : false
				},
				"tariffOptions" : {
					"ext_type" : "list-attribute",
					"required" : false,
					"type" : "text",
					"values" : [ "DATAFLAT_ALLNET", "ROAMING_DATA_50",
							"SMS_ALLNET", "PACKAGE_100", "ALLNET_FLEX",
							"PACKAGE_200", "LANDLINEFLAT", "SMSFLAT",
							"VOICEFLAT", "ALLNET24", "ROAMING_VOICE_50",
							"COMMUNITYFLAT", "DATAFLAT_BIG", "DATAFLAT_YEAR",
							"DATAFLAT", "DATAFLAT_SMALL" ],
					"array" : true
				},
				"storeCallData" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "text",
					"values" : [ "DELETE_IMMEDIATELY", "COMPLETE", "ANONYMOUS" ],
					"array" : false
				}
			},
			"array" : false
		},
		"personalSettings" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "de.s2.sim.simyo.api.domain.order.PersonalSettings",
			"attributes" : {
				"person" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "de.s2.sim.simyo.api.domain.person.Person",
					"attributes" : {
						"phone" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "DomainValue",
							"array" : false
						},
						"email" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "DomainValue",
							"array" : false
						},
						"birthday" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "org.joda.time.DateTime",
							"format" : "dd-MM-yyyy",
							"array" : false
						},
						"title" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"values" : [ "PROF_DR", "PROF", "DR", "NONE" ],
							"array" : false
						},
						"titleName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"firstName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"lastName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"salutation" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"values" : [ "COMPANY", "UNKNOWN", "GENERIC", "MR",
									"MRS" ],
							"array" : false
						}
					},
					"array" : false
				},
				"billingAddress" : {
					"ext_type" : "single-attribute",
					"required" : true,
					"type" : "de.s2.sim.simyo.api.domain.address.UserAddress",
					"attributes" : {
						"company" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"city" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"street" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"zip" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"addressSupplement" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"streetNo" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						}
					},
					"array" : false
				},
				"shippingAddress" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "de.s2.sim.simyo.api.domain.address.UserAddress",
					"attributes" : {
						"company" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"city" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"street" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"zip" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"addressSupplement" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"streetNo" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						}
					},
					"array" : false
				},
				"shippingPerson" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "de.s2.sim.simyo.api.domain.person.BasicPerson",
					"attributes" : {
						"title" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"values" : [ "PROF_DR", "PROF", "DR", "NONE" ],
							"array" : false
						},
						"titleName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"firstName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"lastName" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"max-length" : null,
							"array" : false
						},
						"salutation" : {
							"ext_type" : "single-attribute",
							"required" : false,
							"type" : "text",
							"values" : [ "COMPANY", "UNKNOWN", "GENERIC", "MR",
									"MRS" ],
							"array" : false
						}
					},
					"array" : false
				}
			},
			"array" : false
		},
		"paymentSettings" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "de.s2.sim.simyo.api.domain.order.PaymentSettings",
			"attributes" : {
				"paymentMethod" : {
					"ext_type" : "single-attribute",
					"required" : false,
					"type" : "de.s2.sim.simyo.api.domain.payment.PaymentMethod",
					"attributes" : {},
					"array" : false
				}
			},
			"array" : false
		},
		"agbAccepted" : {
			"ext_type" : "single-attribute",
			"required" : true,
			"type" : "boolean",
			"array" : false
		}
	}
}