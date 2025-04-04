import { Injectable } from '@angular/core';

import * as nsfwjs from 'nsfwjs';
import * as tf from '@tensorflow/tfjs-core';
import '@tensorflow/tfjs-backend-cpu';
@Injectable({
  providedIn: 'root',
})
export class NsfwDetectionService {
  constructor() {}
  private model: any;

  async loadModel() {
    this.model = await nsfwjs.load();
  }
  async isImageSafe(file: File) {
    const img = await this.loadImage(file);
    const predictions = await this.model.classify(img);
    const isUnsafe = predictions.some(
      (p: any) =>
        ['Porn', 'Hentai'].includes(p.className) && p.probability > 0.75
    );
    return !isUnsafe;
  }

  private loadImage(file: File): Promise<HTMLImageElement> {
    const img = new Image();
    img.src = URL.createObjectURL(file);
    return new Promise((resolve) => {
      img.onload = () => resolve(img);
    });
  }
}
