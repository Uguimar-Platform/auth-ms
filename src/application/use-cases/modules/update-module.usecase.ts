import { HttpStatus, Inject, Injectable, Module } from '@nestjs/common';
import { ModuleModel } from 'src/domain/models/module.model';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';

@Injectable()
export class UpdateModuleUseCase {
  constructor(
    @Inject('ModuleRepository') private moduleRepository: ModuleRepositoryPort,
  ) {}

  async execute(id: number, updateModule: ModuleModel) {
    await this.moduleRepository.updateModule(id, updateModule);
    return {
      status: HttpStatus.OK,
      message: 'Module updated successfully',
    };
  }
}
