import { TestBed } from '@angular/core/testing';

import { NsfwDetectionService } from './nsfw-detection.service';

describe('NsfwDetectionService', () => {
  let service: NsfwDetectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NsfwDetectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
