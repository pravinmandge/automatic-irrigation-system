
CREATE TABLE "PLOT"(
                       "ID" varchar(36) NOT NULL,
                       "NAME" varchar(30),
                       "SENSOR_URL" varchar(50)
);
ALTER TABLE "PLOT" ADD CONSTRAINT "PK_PLOT" PRIMARY KEY("ID");

CREATE  TABLE "SLOT"(
                        "ID" varchar(36) NOT NULL,
                        "STATUS" varchar(30),
                        "WATER_NEEDED" INT,
                        "PLOT_ID" varchar(36),
                        "RETRY_COUNT" INT,
                        "LAST_RETRY_TIME" BIGINT,
                        "DURATION_IN_MIN" INT,
                        "INTERVAL_IN_MIN" INT
);
ALTER TABLE "SLOT" ADD CONSTRAINT "PK_SLOT" PRIMARY KEY("ID");

CREATE  TABLE "PLOT_IRRIGATION"(
                                   "ID" varchar(36) NOT NULL,
                                   "STATUS" varchar(30),
                                   "WATER_NEEDED_IN_MM" INT,
                                   "PLOT_ID" varchar(36),
                                   "LAST_RETRY_TIME" BIGINT,
                                   "DURATION_IN_MIN" INT,
                                   "INTERVAL_IN_MIN" INT,
                                   "NEXT_IRRIGATION_TIME" BIGINT,
                                   "CREATED_TIME" BIGINT,
                                   "UPDATED_TIME" BIGINT
);

ALTER TABLE "SLOT" ADD CONSTRAINT "FK_SLOT_PLOT_ID" FOREIGN KEY("PLOT_ID") REFERENCES "PLOT"("ID");
ALTER TABLE "PLOT_IRRIGATION" ADD CONSTRAINT "FK_PLOT_IRRIGATION_PLOT_ID" FOREIGN KEY("PLOT_ID") REFERENCES "PLOT"("ID");
