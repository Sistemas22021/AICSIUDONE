'use client'

import { useState } from 'react'
import { Check, ChevronRight, Camera, Fingerprint, Shield, FileText, CheckCircle2, AlertCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface StepperStep {
  id: number
  title: string
  description: string
  icon: React.ReactNode
}

const steps: StepperStep[] = [
  {
    id: 1,
    title: 'Datos Personales',
    description: 'Ingrese información básica del detenido',
    icon: <FileText size={20} />
  },
  {
    id: 2,
    title: 'Captura de Huellas',
    description: 'Registro biométrico de huellas dactilares',
    icon: <Fingerprint size={20} />
  },
  {
    id: 3,
    title: 'Fotografía',
    description: 'Captura de fotografía frontal y lateral',
    icon: <Camera size={20} />
  },
  {
    id: 4,
    title: 'Lectura de Derechos',
    description: 'Lectura de Miranda y derechos constitucionales',
    icon: <Shield size={20} />
  },
  {
    id: 5,
    title: 'Representación Legal',
    description: 'Confirmación de abogado presente o derecho a uno',
    icon: <FileText size={20} />
  },
  {
    id: 6,
    title: 'Checklist Obligatorio',
    description: 'Verificación final de cumplimiento de procedimientos',
    icon: <CheckCircle2 size={20} />
  }
]

export function GuardOfficerStepper() {
  const [currentStep, setCurrentStep] = useState(1)
  const [completedSteps, setCompletedSteps] = useState<number[]>([])

  // Step 1: Personal Data
  const [personalData, setPersonalData] = useState({
    fullName: '',
    dateOfBirth: '',
    idNumber: '',
    residence: ''
  })

  // Step 4: Rights Acknowledgment
  const [rightsAcknowledged, setRightsAcknowledged] = useState({
    mirandaRead: false,
    understands: false,
    wantsToSpeak: false
  })

  // Step 5: Legal Representation
  const [legalRep, setLegalRep] = useState({
    lawyerPresent: false,
    lawyerName: '',
    wantsLawyer: false
  })

  // Step 6: Mandatory Checklist
  const [checklist, setChecklist] = useState({
    fingerprintsCaptured: false,
    photoCaptured: false,
    medicalEval: false,
    mirandaAcknowledged: false,
    lawyerConfirmed: false,
    physicalCondition: false,
    allergies: false,
    medications: false,
    mentalHealth: false,
    consentRecorded: false
  })

  const handleNextStep = () => {
    if (currentStep < steps.length) {
      setCompletedSteps([...completedSteps, currentStep])
      setCurrentStep(currentStep + 1)
    }
  }

  const handlePrevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1)
    }
  }

  const markStepComplete = () => {
    if (!completedSteps.includes(currentStep)) {
      setCompletedSteps([...completedSteps, currentStep])
    }
    handleNextStep()
  }

  const isStepCompleted = (stepId: number) => completedSteps.includes(stepId)
  const isStepActive = (stepId: number) => currentStep === stepId

  const getStepBgColor = (stepId: number) => {
    if (isStepCompleted(stepId)) return 'bg-chart-3 text-background'
    if (isStepActive(stepId)) return 'bg-primary text-primary-foreground'
    return 'bg-secondary text-foreground'
  }

  return (
    <div className="flex flex-col h-full gap-6 p-6">
      {/* Header */}
      <div>
        <h2 className="text-2xl font-bold text-foreground mb-1">Procesar Individuo</h2>
        <p className="text-sm text-muted-foreground">Procesamiento de Ingreso - Procedimiento Estándar de Custodia</p>
      </div>

      {/* Stepper Progress Indicator */}
      <Card className="bg-card border-border p-6">
        <div className="flex items-start gap-4 overflow-x-auto pb-4">
          {steps.map((step, idx) => (
            <div key={step.id} className="flex items-center gap-2 flex-shrink-0">
              {/* Step Circle */}
              <button
                onClick={() => setCurrentStep(step.id)}
                className={`w-12 h-12 rounded-full flex items-center justify-center font-bold transition-all ${getStepBgColor(step.id)}`}
              >
                {isStepCompleted(step.id) ? (
                  <Check size={20} />
                ) : (
                  <span>{step.id}</span>
                )}
              </button>

              {/* Step Label */}
              <div className="hidden sm:block min-w-max">
                <p className={`text-sm font-bold ${isStepActive(step.id) ? 'text-primary' : 'text-foreground'}`}>
                  {step.title}
                </p>
                <p className="text-xs text-muted-foreground">{step.description}</p>
              </div>

              {/* Connector Line */}
              {idx < steps.length - 1 && (
                <div
                  className={`w-8 h-0.5 transition-all ${isStepCompleted(step.id) ? 'bg-chart-3' : 'bg-border'
                    }`}
                />
              )}
            </div>
          ))}
        </div>

        {/* Progress Bar */}
        <div className="mt-4 h-2 bg-secondary rounded-full overflow-hidden">
          <div
            className="h-full bg-primary transition-all"
            style={{
              width: `${((completedSteps.length + 1) / steps.length) * 100}%`
            }}
          />
        </div>
        <p className="text-xs text-muted-foreground mt-2">
          Paso {currentStep} de {steps.length}
        </p>
      </Card>

      {/* Step Content */}
      <Card className="bg-card border-border flex-1 p-6 overflow-y-auto">
        {currentStep === 1 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <FileText className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Datos Personales</h3>
                <p className="text-sm text-muted-foreground">Ingrese la información básica del detenido</p>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">Nombre Completo</label>
                <input
                  type="text"
                  value={personalData.fullName}
                  onChange={(e) => setPersonalData({ ...personalData, fullName: e.target.value })}
                  placeholder="Ej: Carlos Mendez López"
                  className="w-full px-4 py-2 bg-secondary border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-foreground mb-2">Fecha de Nacimiento</label>
                  <input
                    type="date"
                    value={personalData.dateOfBirth}
                    onChange={(e) => setPersonalData({ ...personalData, dateOfBirth: e.target.value })}
                    className="w-full px-4 py-2 bg-secondary border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-foreground mb-2">Cédula / Pasaporte</label>
                  <input
                    type="text"
                    value={personalData.idNumber}
                    onChange={(e) => setPersonalData({ ...personalData, idNumber: e.target.value })}
                    placeholder="Ej: 1-234-567890"
                    className="w-full px-4 py-2 bg-secondary border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-2">Dirección de Residencia</label>
                <input
                  type="text"
                  value={personalData.residence}
                  onChange={(e) => setPersonalData({ ...personalData, residence: e.target.value })}
                  placeholder="Ej: Avenida Principal, Sector 7"
                  className="w-full px-4 py-2 bg-secondary border border-border rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
            </div>
          </div>
        )}

        {currentStep === 2 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <Fingerprint className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Captura de Huellas Dactilares</h3>
                <p className="text-sm text-muted-foreground">Registro biométrico de huellas</p>
              </div>
            </div>

            <div className="space-y-4">
              <div className="bg-secondary border border-border rounded-lg p-8 text-center">
                <div className="w-24 h-24 bg-primary/10 rounded-lg mx-auto mb-4 flex items-center justify-center border-2 border-dashed border-primary">
                  <Fingerprint size={48} className="text-primary" />
                </div>
                <p className="text-foreground font-medium mb-2">Dispositivo de Captura de Huellas</p>
                <p className="text-sm text-muted-foreground mb-4">Conectar escáner biométrico y capturar ambas manos</p>
                <Button className="bg-primary hover:bg-primary/90 text-primary-foreground">
                  Iniciar Captura Biométrica
                </Button>
              </div>

              <div className="space-y-2">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" className="w-4 h-4" defaultChecked />
                  <span className="text-sm text-foreground">✓ Huellas de mano derecha capturadas</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" className="w-4 h-4" defaultChecked />
                  <span className="text-sm text-foreground">✓ Huellas de mano izquierda capturadas</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" className="w-4 h-4" />
                  <span className="text-sm text-foreground">{'Calidad de captura verificada (>95%)'}</span>
                </label>
              </div>
            </div>
          </div>
        )}

        {currentStep === 3 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <Camera className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Fotografía de Detención</h3>
                <p className="text-sm text-muted-foreground">Captura frontal y de perfil</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bg-secondary border border-border rounded-lg p-6 text-center">
                <div className="w-full aspect-square bg-primary/10 rounded-lg mb-4 flex items-center justify-center border-2 border-dashed border-primary">
                  <Camera size={48} className="text-primary" />
                </div>
                <p className="text-foreground font-medium text-sm">Fotografía Frontal</p>
              </div>

              <div className="bg-secondary border border-border rounded-lg p-6 text-center">
                <div className="w-full aspect-square bg-primary/10 rounded-lg mb-4 flex items-center justify-center border-2 border-dashed border-primary">
                  <Camera size={48} className="text-primary" />
                </div>
                <p className="text-foreground font-medium text-sm">Fotografía de Perfil</p>
              </div>
            </div>

            <div className="space-y-2">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="w-4 h-4" />
                <span className="text-sm text-foreground">Fotografía frontal clara y bien iluminada</span>
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="w-4 h-4" />
                <span className="text-sm text-foreground">Fotografía de perfil izquierdo capturada</span>
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="w-4 h-4" />
                <span className="text-sm text-foreground">Fotografía de perfil derecho capturada</span>
              </label>
            </div>
          </div>
        )}

        {currentStep === 4 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <Shield className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Lectura de Derechos Constitucionales</h3>
                <p className="text-sm text-muted-foreground">Advertencia Miranda - Requisito Obligatorio</p>
              </div>
            </div>

            <div className="bg-primary/5 border border-primary/30 rounded-lg p-4 mb-4">
              <p className="text-sm text-foreground leading-relaxed font-mono">
                "Usted tiene el derecho de permanecer en silencio. Si renuncia a este derecho, cualquier cosa que diga puede
                ser usada en su contra en un tribunal. Tiene derecho a un abogado. Si no puede pagar uno, se le proporcionará
                uno. ¿Entiende estos derechos?"
              </p>
            </div>

            <div className="space-y-4">
              <label className="flex items-center gap-3 p-3 bg-secondary rounded-lg cursor-pointer hover:bg-secondary/80 transition-all">
                <input
                  type="checkbox"
                  checked={rightsAcknowledged.mirandaRead}
                  onChange={(e) => setRightsAcknowledged({ ...rightsAcknowledged, mirandaRead: e.target.checked })}
                  className="w-5 h-5"
                />
                <span className="text-foreground font-medium">He leído los derechos Miranda completos</span>
              </label>

              <label className="flex items-center gap-3 p-3 bg-secondary rounded-lg cursor-pointer hover:bg-secondary/80 transition-all">
                <input
                  type="checkbox"
                  checked={rightsAcknowledged.understands}
                  onChange={(e) => setRightsAcknowledged({ ...rightsAcknowledged, understands: e.target.checked })}
                  className="w-5 h-5"
                />
                <span className="text-foreground font-medium">El detenido declara entender estos derechos</span>
              </label>

              <label className="flex items-center gap-3 p-3 bg-secondary rounded-lg cursor-pointer hover:bg-secondary/80 transition-all">
                <input
                  type="checkbox"
                  checked={rightsAcknowledged.wantsToSpeak}
                  onChange={(e) => setRightsAcknowledged({ ...rightsAcknowledged, wantsToSpeak: e.target.checked })}
                  className="w-5 h-5"
                />
                <span className="text-foreground font-medium">Desea hablar con un oficial de investigación</span>
              </label>
            </div>
          </div>
        )}

        {currentStep === 5 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <Shield className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Representación Legal</h3>
                <p className="text-sm text-muted-foreground">Confirmación de abogado presente o derecho a uno</p>
              </div>
            </div>

            <div className="space-y-4">
              <label className="flex items-center gap-3 p-4 bg-secondary rounded-lg cursor-pointer hover:bg-secondary/80 transition-all border border-border">
                <input
                  type="radio"
                  name="lawyer"
                  checked={legalRep.lawyerPresent}
                  onChange={() => setLegalRep({ ...legalRep, lawyerPresent: true, wantsLawyer: false })}
                  className="w-5 h-5"
                />
                <span className="text-foreground font-medium">Abogado presente en este momento</span>
              </label>

              {legalRep.lawyerPresent && (
                <div className="ml-8">
                  <label className="block text-sm font-medium text-foreground mb-2">Nombre del Abogado</label>
                  <input
                    type="text"
                    value={legalRep.lawyerName}
                    onChange={(e) => setLegalRep({ ...legalRep, lawyerName: e.target.value })}
                    placeholder="Nombre y cédula del profesional"
                    className="w-full px-4 py-2 bg-primary/10 border border-primary/30 rounded-lg text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>
              )}

              <label className="flex items-center gap-3 p-4 bg-secondary rounded-lg cursor-pointer hover:bg-secondary/80 transition-all border border-border">
                <input
                  type="radio"
                  name="lawyer"
                  checked={legalRep.wantsLawyer}
                  onChange={() => setLegalRep({ ...legalRep, wantsLawyer: true, lawyerPresent: false })}
                  className="w-5 h-5"
                />
                <span className="text-foreground font-medium">Deseo comunicarme con un abogado antes de hablar</span>
              </label>

              <div className="bg-primary/5 border border-primary/30 rounded-lg p-4">
                <p className="text-xs text-muted-foreground">
                  ⚠️ Si el detenido solicita un abogado, toda sesión de interrogatorio debe detenerse hasta que se haya
                  proporcionado representación legal o se haya consultado con un abogado de su elección.
                </p>
              </div>
            </div>
          </div>
        )}

        {currentStep === 6 && (
          <div className="space-y-4">
            <div className="flex items-center gap-2 mb-6">
              <CheckCircle2 className="text-primary" size={24} />
              <div>
                <h3 className="text-xl font-bold text-foreground">Checklist Obligatorio Final</h3>
                <p className="text-sm text-muted-foreground">Verificación de cumplimiento de procedimientos</p>
              </div>
            </div>

            <div className="space-y-3">
              <div className="bg-primary/5 border border-primary/30 rounded-lg p-4 mb-4">
                <p className="text-xs text-foreground flex items-center gap-2">
                  <AlertCircle size={16} className="text-primary" />
                  Todos los elementos marcados son obligatorios antes de sellar el acta de ingreso.
                </p>
              </div>

              <div className="grid grid-cols-2 gap-4">
                {Object.entries({
                  fingerprintsCaptured: 'Huellas dactilares capturadas',
                  photoCaptured: 'Fotografía completa (3 vistas)',
                  medicalEval: 'Evaluación médica realizada',
                  mirandaAcknowledged: 'Derechos Miranda confirmados',
                  lawyerConfirmed: 'Representación legal confirmada',
                  physicalCondition: 'Condición física documentada',
                  allergies: 'Alergias y medicamentos registrados',
                  medications: 'Medicamentos actuales verificados',
                  mentalHealth: 'Estado mental evaluado',
                  consentRecorded: 'Consentimiento grabado en voz'
                }).map(([key, label]) => (
                  <label key={key} className="flex items-center gap-2 p-2 hover:bg-secondary/50 rounded cursor-pointer transition-all">
                    <input
                      type="checkbox"
                      checked={checklist[key as keyof typeof checklist]}
                      onChange={(e) => setChecklist({ ...checklist, [key]: e.target.checked })}
                      className="w-4 h-4"
                    />
                    <span className="text-sm text-foreground">{label}</span>
                  </label>
                ))}
              </div>

              <div className="bg-chart-3/10 border border-chart-3/30 rounded-lg p-4 mt-4">
                <p className="text-sm font-bold text-chart-3 flex items-center gap-2">
                  <Check size={18} />
                  Todos los procedimientos completados correctamente
                </p>
                <p className="text-xs text-muted-foreground mt-2">
                  El expediente está listo para sellado criptográfico e inmutabilidad.
                </p>
              </div>
            </div>
          </div>
        )}
      </Card>

      {/* Navigation Buttons */}
      <div className="flex justify-between gap-4">
        <Button
          onClick={handlePrevStep}
          disabled={currentStep === 1}
          className="bg-secondary hover:bg-muted text-foreground border border-border disabled:opacity-50"
          variant="outline"
        >
          Atrás
        </Button>

        <div className="flex gap-2">
          {currentStep < steps.length && (
            <>
              <Button
                onClick={() => setCurrentStep(steps.length)}
                variant="outline"
                className="bg-secondary hover:bg-muted text-foreground border border-border"
              >
                Saltar al Final
              </Button>
              <Button
                onClick={markStepComplete}
                className="bg-primary hover:bg-primary/90 text-primary-foreground gap-2"
              >
                Siguiente
                <ChevronRight size={18} />
              </Button>
            </>
          )}

          {currentStep === steps.length && (
            <Button className="bg-chart-3 hover:bg-chart-3/90 text-background gap-2">
              <Check size={18} />
              Sellar y Completar Ingreso
            </Button>
          )}
        </div>
      </div>
    </div>
  )
}
