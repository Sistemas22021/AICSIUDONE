import { EvidenceRecord, TwistDirection, PercussionType } from '../types/evidence';

export const MOCK_DATABASE: EvidenceRecord[] = [
  {
    id: 'DB-001',
    createdAt: '2026-05-10',
    expediente: 'EXP-2026-089',
    calibre: '9mm Parabellum',
    estrias: '6',
    twist: TwistDirection.DEXTRORSUM,
    percussion: PercussionType.CENTRAL,
    marca: 'Glock',
    previewUrl: null
  },
  {
    id: 'DB-002',
    createdAt: '2026-04-20',
    expediente: 'EXP-2026-055',
    calibre: '9mm Parabellum',
    estrias: '6',
    twist: TwistDirection.DEXTRORSUM,
    percussion: PercussionType.CENTRAL,
    marca: 'Glock Inc.', // Same class, slight brand variation
    previewUrl: null
  },
  {
    id: 'DB-003',
    createdAt: '2025-11-05',
    expediente: 'EXP-2025-410',
    calibre: '9mm Parabellum',
    estrias: '4', // Different estrias
    twist: TwistDirection.SINISTRORSUM, // Different twist
    percussion: PercussionType.CENTRAL,
    marca: 'Smith & Wesson',
    previewUrl: null
  },
  {
    id: 'DB-004',
    createdAt: '2024-08-22',
    expediente: 'EXP-2024-102',
    calibre: '.45 ACP', // Different caliber -> Should be discarded immediately
    estrias: '6',
    twist: TwistDirection.DEXTRORSUM,
    percussion: PercussionType.CENTRAL,
    marca: 'Colt',
    previewUrl: null
  }
];
