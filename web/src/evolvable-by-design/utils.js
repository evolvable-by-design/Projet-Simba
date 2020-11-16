export async function executeProcess(resource, key, nextKey, parameters) {
  console.log(key)
  const link = resource.getRelation(key, 1).getOrUndefined()
  console.log(link)

  if (link !== undefined) {
    const operation = link.operation
    const result = await operation.invoke(parameters)
    const resultResource = result.data

    return await executeProcess(resultResource, nextKey || key, nextKey, parameters)
  } else {
    return resource
  }
}
