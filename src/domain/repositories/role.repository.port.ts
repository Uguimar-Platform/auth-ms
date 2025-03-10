import { RoleModel } from "../models/role.model";

export interface RoleRepositoryPort {
  createRole(role: RoleModel): Promise<RoleModel>;
}