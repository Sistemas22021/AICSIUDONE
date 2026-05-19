-- =========================================
-- INCIDENT MANAGEMENT SYSTEM DATABASE
-- =========================================

-- OPTIONAL RESET
DROP TABLE IF EXISTS assignments CASCADE;
DROP TABLE IF EXISTS incidents CASCADE;
DROP TABLE IF EXISTS patrols CASCADE;

-- =========================================
-- TABLE: PATROLS
-- =========================================

CREATE TABLE patrols (
                         id BIGSERIAL PRIMARY KEY,
                         code VARCHAR(50) NOT NULL UNIQUE,
                         officer_name VARCHAR(100) NOT NULL,
                         latitude DOUBLE PRECISION NOT NULL,
                         longitude DOUBLE PRECISION NOT NULL,
                         coverage_radius DOUBLE PRECISION,
                         status VARCHAR(30) NOT NULL,
                         last_updated TIMESTAMP
);

-- =========================================
-- TABLE: INCIDENTS
-- =========================================

CREATE TABLE incidents (
                           id BIGSERIAL PRIMARY KEY,
                           type VARCHAR(100) NOT NULL,
                           description TEXT NOT NULL,
                           latitude DOUBLE PRECISION NOT NULL,
                           longitude DOUBLE PRECISION NOT NULL,

                           status VARCHAR(30) NOT NULL
                               CHECK (status IN ('ACTIVE', 'IN_PROGRESS', 'CLOSED')),

                           priority VARCHAR(20) NOT NULL
                               CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           closed_at TIMESTAMP,

                           assigned_patrol_id BIGINT,

                           CONSTRAINT fk_incident_patrol
                               FOREIGN KEY (assigned_patrol_id)
                                   REFERENCES patrols(id)
                                   ON DELETE SET NULL
);

-- =========================================
-- TABLE: ASSIGNMENTS
-- =========================================

CREATE TABLE assignments (
                             id BIGSERIAL PRIMARY KEY,
                             assigned_at TIMESTAMP NOT NULL,
                             finished_at TIMESTAMP,
                             notes TEXT,

                             incident_id BIGINT NOT NULL,
                             patrol_id BIGINT NOT NULL,

                             CONSTRAINT fk_assignment_incident
                                 FOREIGN KEY (incident_id)
                                     REFERENCES incidents(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_assignment_patrol
                                 FOREIGN KEY (patrol_id)
                                     REFERENCES patrols(id)
                                     ON DELETE CASCADE
);

-- =========================================
-- TABLE: OPERATIONAL_LOGS
-- =========================================

CREATE TABLE operational_logs (
                                  id BIGSERIAL PRIMARY KEY,
                                  event_type VARCHAR(50) NOT NULL,
                                  description TEXT,

                                  incident_id BIGINT,
                                  patrol_id BIGINT,

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_log_incident
                                      FOREIGN KEY (incident_id)
                                          REFERENCES incidents(id)
                                          ON DELETE SET NULL,

                                  CONSTRAINT fk_log_patrol
                                      FOREIGN KEY (patrol_id)
                                          REFERENCES patrols(id)
                                          ON DELETE SET NULL
);