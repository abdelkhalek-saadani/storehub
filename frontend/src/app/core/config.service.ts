import { Injectable } from '@angular/core';

export interface AppConfig {
  kcUrl: string;
  orderApiUrl: string;
  catalogApiUrl: string;
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private config!: AppConfig;

  async load(): Promise<void> {
    const res = await fetch('/config.json');
    this.config = await res.json();
  }

  setConfig(config: AppConfig) {
    this.config = config;
  }

  get(): AppConfig {
    return this.config;
  }
}
