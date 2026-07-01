package naranja.custodia_360.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Testimony{
        @Id
        private String sessionId;
        
        private String cedula;
        private String caseNumber;

        //rutas locales
        private String audioPath;
        private String originalTextPath;
        private String modifiedTextPath;

        public Testimony (){}

        public Testimony (String sessionId, String cedula, String caseNumber, String audioPath, String originalTextPath, String modifiedTextPath){
                this.sessionId = sessionId;
                this.cedula = cedula;
                this.caseNumber = caseNumber;
                this.audioPath = audioPath;
                this.originalTextPath = originalTextPath;
                this.modifiedTextPath = modifiedTextPath;
        }

        //Getters
        public String getSessionId() { return sessionId; }
        public String getCedula() { return cedula; }
        public String getCaseNumber() { return caseNumber; }
        public String getAudioPath() { return audioPath; }
        public String getOriginalTextPath() { return originalTextPath; }
        public String getModifiedTextPath() { return modifiedTextPath; }

        //setters
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public void setCedula(String cedula) { this.cedula = cedula; }
        public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
        public void setAudioPath(String audioPath) { this.audioPath = audioPath; }
        public void setOriginalTextPath(String originalTextPath) { this.originalTextPath = originalTextPath; }
        public void setModifiedTextPath(String modifiedTextPath) { this.modifiedTextPath = modifiedTextPath; }
}