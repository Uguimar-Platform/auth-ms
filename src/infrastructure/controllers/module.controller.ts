import { Controller, HttpStatus, ParseIntPipe } from "@nestjs/common";
import { MessagePattern, Payload } from "@nestjs/microservices";
import { CreateModuleUseCase } from "src/application/use-cases/modules/create-module.usecase";
import { CreateModuleDto } from "../dto/create-module.dto";
import { FindModuleByIdUseCase } from "src/application/use-cases/modules/find-module-by-id.usecase";


@Controller()
export class ModuleController {
  /**
   *
   * @param createModuleUseCase  Use case to inject into providers
   *    */
  constructor(
    private readonly createModuleUseCase: CreateModuleUseCase,
    private readonly findModuleByIdUseCase: FindModuleByIdUseCase
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
  findOne(@Payload('id', ParseIntPipe) id: number) {
    return this.findModuleByIdUseCase.execute(id);
  }
}
