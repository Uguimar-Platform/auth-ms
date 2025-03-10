import { Controller, HttpStatus } from "@nestjs/common";
import { MessagePattern, Payload } from "@nestjs/microservices";
import { CreateModuleUseCase } from "src/application/use-cases/modules/create-module.usecase";
import { CreateModuleDto } from "../dto/create-module.dto";


@Controller()
export class ModuleController {

  /**
   *
   * @param createModuleUseCase  Use case to inject into providers
   *    */
  constructor(private readonly createModuleUseCase: CreateModuleUseCase) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  @MessagePattern({cmd: 'create_module'})
  async createModule(@Payload() module: CreateModuleDto) {
    const userCreated = await this.createModuleUseCase.execute(module);
    return userCreated;
  }
}
