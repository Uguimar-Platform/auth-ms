import { Inject, Injectable, HttpStatus } from '@nestjs/common';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';
import { PaginatedFilterDto } from 'src/infrastructure/dto/list-module.filter.dto';

@Injectable()
export class ListModulePaginatedUseCase {
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

  async execute(filter: PaginatedFilterDto) {
    const moduleexecute =
      await this.moduleRepository.listModulePaginated(filter);
    return {
      status: HttpStatus.OK,
      data: moduleexecute,
    };
  }
}