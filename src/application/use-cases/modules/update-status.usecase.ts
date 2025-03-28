import { Inject, Injectable, HttpStatus } from '@nestjs/common';
import { ModuleModel } from 'src/domain/models/module.model';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';

@Injectable()
export class UpdateStatusModuleUseCase {
  /**
   *
   * @param moduleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('ModuleRepository') private moduleRepository: ModuleRepositoryPort,
  ) {}


  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */
 
  async execute(id: number, status: number) {
    const moduleexecute = await this.moduleRepository.updateModuleStatus(
      id,
      status,
    );
    console.log('usecase',moduleexecute);
    return {
      status: HttpStatus.CREATED,
      message: 'Module changed status successfully',
    };
  }
}