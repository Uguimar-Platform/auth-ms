import { PermissionsModel } from "../models/permissions.model";

export interface PermissionsRepositoryPort {
  createPermission(permisison: PermissionsModel);
}