import { Controller, HttpStatus, ParseIntPipe } from "@nestjs/common";
import { MessagePattern, Payload } from "@nestjs/microservices";
import { CreateModuleUseCase } from "src/application/use-cases/modules/create-module.usecase";
import { CreateModuleDto } from "../dto/modules/create-module.dto";
import { FindModuleByIdUseCase } from "src/application/use-cases/modules/find-module-by-id.usecase";
import { UpdateModuleUseCase } from "src/application/use-cases/modules/update-module.usecase";
import { UpdateModuleDto } from "../dto/modules/update-module.dto";
import { UpdateStatusModuleUseCase } from "src/application/use-cases/modules/update-status.usecase";
import { ListModulePaginatedUseCase } from "src/application/use-cases/modules/list-module-paginated.usecase";
import { PaginatedFilterDto } from "../dto/list-module.filter.dto";


@Controller()
export class ModuleController {
  /**
   *
   * @param createModuleUseCase  Use case to inject into providers
   *    */
  constructor(
    private readonly createModuleUseCase: CreateModuleUseCase,
    private readonly findModuleByIdUseCase: FindModuleByIdUseCase,
    private readonly updateModuleUseCase: UpdateModuleUseCase,
    private readonly updateStatusModuleUseCase: UpdateStatusModuleUseCase,
    private readonly listModulePaginatedUseCase: ListModulePaginatedUseCase,
  ) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  @MessagePattern({ cmd: 'create_module' })
  async createModule(@Payload() module: CreateModuleDto) {
    const userCreated = await this.createModuleUseCase.execute(module);
    return userCreated;
  }

  /**
   *
   * @param id The id of the module to be found.
   * @returns The module found.
   *
   */
  @MessagePattern({ cmd: 'find_module_id' })
  async findOne(@Payload('id', ParseIntPipe) id: number) {
    return this.findModuleByIdUseCase.execute(id);
  }

  @MessagePattern({ cmd: 'update_module' })
  async update(@Payload() payload: UpdateModuleDto, @Payload('id') id: number) {
    return await this.updateModuleUseCase.execute(id, payload);
  }

  @MessagePattern({ cmd: 'update_module_status' })
  async updateStatus(@Payload() payload: { id: number; status: number }) {
    const { id, status } = payload;
    return await this.updateStatusModuleUseCase.execute(id, status);
  }

  @MessagePattern({ cmd: 'list_module_paginated' })
  async list(@Payload() paginated: PaginatedFilterDto) {
    return await this.listModulePaginatedUseCase.execute(paginated);
  }
}
